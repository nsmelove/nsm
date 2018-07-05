package com.nsm.core.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.nsm.bean.ErrorCode;
import com.nsm.common.redis.RedisUtil;
import com.nsm.common.utils.BytesUtils;
import com.nsm.core.entity.Contact;
import com.nsm.core.dao.ContactDao;
import com.nsm.core.exception.BusinessException;
import com.nsm.core.pojo.ContactInfo;
import com.nsm.core.pojo.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Tuple;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by nieshuming on 2018/7/3
 */

@Service
public class ContactService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final int CONTACT_REQ_EXP_TIME = 2 * 30 * 24 * 3600;
    private JedisCluster jedis  = RedisUtil.getJedisCluster();
    @Resource
    private ContactDao contactDao;
    @Resource
    private UserService userService;

    private byte[] getReqContactKey(long userId){
        return new StringBuilder("ctReqs:").append(userId).toString().getBytes();
    }

    private byte[] getRecReqContactKey(long userId){
        return new StringBuilder("ctRecReqs:").append(userId).toString().getBytes();
    }

    private byte[] getContactKey(long userId){
        return new StringBuilder("cts:").append(userId).toString().getBytes();
    }

    /**
     * 请求添加联系人
     *
     * @param userId 当前用户Id
     * @param contactId 联系人Id
     */
    public void reqContact(long userId, long contactId){
        if(userService.getUser(contactId) == null){
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        if (isContact(userId, contactId)) {
            throw new BusinessException(ErrorCode.ALREADY_CONTACT);
        }
        byte[] targetReqKey = getReqContactKey(contactId);
        if(jedis.zscore(targetReqKey, BytesUtils.getBytes(userId)) != null) {
            throw new BusinessException(ErrorCode.TARGET_ALREADY_CONTACT_REQ);
        }

        byte[] reqKey = getReqContactKey(userId);
        jedis.zadd(reqKey, System.currentTimeMillis(), BytesUtils.getBytes(contactId));
        jedis.expire(reqKey, CONTACT_REQ_EXP_TIME);

        byte[] recReqKey = getRecReqContactKey(contactId);
        jedis.zadd(recReqKey, System.currentTimeMillis(), BytesUtils.getBytes(userId));
        jedis.expire(recReqKey, CONTACT_REQ_EXP_TIME);
    }

    public List<ContactInfo> contactReqList(long userId){
        byte[] reqKey = getReqContactKey(userId);
        Set<Tuple> scoreElements = jedis.zrevrangeByScoreWithScores(reqKey, Double.MAX_VALUE, 0);
        List<ContactInfo> reqInfos = Lists.newArrayListWithCapacity(scoreElements.size());
        Long now = System.currentTimeMillis();
        for(Tuple scoreElement : scoreElements){
            if(now - scoreElement.getScore() > CONTACT_REQ_EXP_TIME * 1000L) {
                jedis.zrem(reqKey, scoreElement.getBinaryElement());
            }else {
                long contactId = BytesUtils.bytesToLong(scoreElement.getBinaryElement());
                UserInfo userInfo = userService.getUserInfo(contactId);
                ContactInfo info = userInfo != null ? ContactInfo.copyUserInfo(userInfo) : new ContactInfo();
                info.setContactId(contactId);
                info.setCreateTime(new Double(scoreElement.getScore()).longValue());
                reqInfos.add(info);
            }
        }
        return reqInfos;
    }

    public List<ContactInfo> contactRecReqList(long userId){
        byte[] reqKey = getRecReqContactKey(userId);
        Set<Tuple> scoreElements = jedis.zrevrangeByScoreWithScores(reqKey, Double.MAX_VALUE, 0);
        List<ContactInfo> recReqInfos = Lists.newArrayListWithCapacity(scoreElements.size());
        Long now = System.currentTimeMillis();
        for(Tuple scoreElement : scoreElements){
            if(now - scoreElement.getScore() > CONTACT_REQ_EXP_TIME * 1000L) {
                jedis.zrem(reqKey, scoreElement.getBinaryElement());
            }else {
                long contactId = BytesUtils.bytesToLong(scoreElement.getBinaryElement());
                UserInfo userInfo = userService.getUserInfo(contactId);
                ContactInfo info = userInfo != null ? ContactInfo.copyUserInfo(userInfo) : new ContactInfo();
                info.setContactId(contactId);
                info.setCreateTime(new Double(scoreElement.getScore()).longValue());
                recReqInfos.add(info);
            }
        }
        return recReqInfos;
    }

    public void acceptContact(long userId, long contactId) {
        //移除收到的请求
        byte[] recReqKey = getRecReqContactKey(userId);
        Long remCount = jedis.zrem(recReqKey, BytesUtils.getBytes(contactId));
        if(remCount == null || remCount == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        //移除对方请求列表
        byte[] reqKey = getReqContactKey(contactId);
        jedis.zrem(reqKey, BytesUtils.getBytes(userId));

        //添加联系人
        Contact contact = Contact.newContact(contactId, userId);
        contactDao.addContact(contact);
        byte[] currContactKey = getContactKey(userId);
        if(jedis.exists(currContactKey)) {
            jedis.zadd(currContactKey, contact.getCreateTime(), BytesUtils.getBytes(contactId));
        }
        byte[] targetContactKey = getContactKey(contactId);
        if(jedis.exists(targetContactKey)) {
            jedis.zadd(targetContactKey, contact.getCreateTime(), BytesUtils.getBytes(userId));
        }
    }

    public void rejectContact(long userId, long contactId) {
        //移除收到的请求
        byte[] recReqKey = getRecReqContactKey(userId);
        Long remCount = jedis.zrem(recReqKey, BytesUtils.getBytes(contactId));
        if(remCount == null || remCount == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        //移除对方请求列表
        byte[] reqKey = getReqContactKey(contactId);
        jedis.zrem(reqKey, BytesUtils.getBytes(userId));

    }

    /**
     * 从数据库中重新加载联系人到缓存
     * @param userId
     * @return
     */
    private Set<Long> reCachedContact(long userId) {
        //从数据库中取并添加到缓存
        List<Contact> contactList = contactDao.getContactsByUserFromOrTo(userId, 0, Integer.MAX_VALUE);
        Set<Long> contactIds = Sets.newHashSetWithExpectedSize(contactList.size());
        Map<byte[],Double> scoreMembers = Maps.newHashMapWithExpectedSize(contactList.size() +1);
        scoreMembers.put(BytesUtils.getBytes(0L), 0.0);
        for(Contact contact : contactList) {
            if(contact.getUserFrom() == userId) {
                contactIds.add(contact.getUserTo());
                scoreMembers.put(BytesUtils.getBytes(contact.getUserTo()), new Long(contact.getCreateTime()).doubleValue());
            }else if(contact.getUserTo() == userId){
                contactIds.add(contact.getUserFrom());
                scoreMembers.put(BytesUtils.getBytes(contact.getUserFrom()), new Long(contact.getCreateTime()).doubleValue());
            }
        }
        byte[] contactKey = getContactKey(userId);
        jedis.zadd(contactKey, scoreMembers);
        return contactIds;
    }

    public boolean isContact(long userId, long targetId){
        byte[] contactKey = getContactKey(userId);
        if(jedis.exists(contactKey)) {
            Double score = jedis.zscore(contactKey, BytesUtils.getBytes(targetId));
            return (score != null && score > 0);
        }else {
            return reCachedContact(userId).contains(targetId);
        }
    }

    public int contactNum(long userId){
        byte[] contactKey = getContactKey(userId);
        if(jedis.exists(contactKey)) {
            return jedis.zcard(contactKey).intValue();
        }else {
            return reCachedContact(userId).size();
        }
    }

    public Set<Long> getContactSet(long userId) {
        Set<Long> contactIds;
        byte[] contactKey = getContactKey(userId);
        Set<byte[]> elements = jedis.zrevrangeByScore(contactKey, Long.MAX_VALUE, 0);
        if(elements.isEmpty()) {
            contactIds = reCachedContact(userId);
        }else {
            contactIds = Sets.newHashSetWithExpectedSize(elements.size());
            for(byte[] element : elements) {
                long contactId = BytesUtils.bytesToLong(element);
                if(contactId > 0) {// 0 默认值，用来保证联系人列表为空时缓存不被清掉
                    contactIds.add(contactId);
                }
            }
        }
        return contactIds;
    }

    public List<ContactInfo> getContacts(long userId, int offset, int limit){
        List<Contact> contactList = contactDao.getContactsByUserFromOrTo(userId, offset, limit);
        List<ContactInfo> contactInfos = Lists.newArrayListWithExpectedSize(contactList.size());
        for(Contact contact : contactList) {
            long contactId = contact.getUserFrom() == userId ? contact.getUserTo() : contact.getUserFrom();
            UserInfo contactUserInfo = userService.getUserInfo(contactId);
            ContactInfo info = contactUserInfo != null ? ContactInfo.copyUserInfo(contactUserInfo) : new ContactInfo();
            info.setContactId(contactId);
            info.setCreateTime(contact.getCreateTime());
            contactInfos.add(info);
        }
        return contactInfos;
    }

    public void removeContact(long userId, long contactId) {
        contactDao.delContactByUserFromAndTo(userId, contactId);

        byte[] currContactKey = getContactKey(userId);
        jedis.zrem(currContactKey, BytesUtils.getBytes(contactId));

        byte[] targetContactKey = getContactKey(contactId);
        jedis.zrem(targetContactKey, BytesUtils.getBytes(userId));
    }
}

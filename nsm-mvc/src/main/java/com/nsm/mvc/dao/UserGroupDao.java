package com.nsm.mvc.dao;

import com.nsm.common.mongodb.MongodbUtil;
import com.nsm.mvc.bean.GroupMember;
import com.nsm.mvc.bean.UserGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/13.
 */
@Repository("userGroupDao")
public class UserGroupDao {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private MongoTemplate template = MongodbUtil.getTemplate();


    public void addUserGroup(UserGroup group){
        template.insert(group);
    }


    public UserGroup getUserGroup(long groupId){
        return template.findById(groupId, UserGroup.class);
    }

    public List<UserGroup> getUserGroups(int offset, int limit){
        Query query = new Query().skip(offset).limit(limit);
        return template.find(query, UserGroup.class);
    }

    public List<UserGroup> getUserGroups(long userId, int offset, int limit){
        Query query = new Query(Criteria.where("memberId").is(userId)).skip(offset).limit(limit);
        return template.find(query, UserGroup.class);
    }

    public void updateUserGroup(long groupId, String groupName, Integer privacy, Boolean silent, List<Long> addSubGIds, List<Long> delSubGIds) {

    }
    public void addGroupMember(GroupMember member){
        template.save(member);
    }

    public List<GroupMember> getGroupMembers(long groupId, int offset, int limit) {
        Query query = Query.query(Criteria.where("groupId").is(groupId)).skip(offset).limit(limit);
        return  template.find(query, GroupMember.class);
    }

    private void initTestData(){
        long groupBegin = 1000000000000000L;
        long userBegin =  1000000000000000L;
        for(int i = 0 ; i < 1000; i++) {
            UserGroup group = new UserGroup();
            long groupId = groupBegin + i;
//            group.setGroupId(groupId);
//            group.setGroupName("群组" + i);
//            group.setCreatorId(groupId);
//            group.setCreateTime(System.currentTimeMillis());
//            addUserGroup(group);
//            System.out.println("add group:" + groupId);
            for(int j = 0; j< 500; j++) {
                GroupMember member = new GroupMember();
                member.setGroupId(groupId);
                member.setMemberId(userBegin + i+ j);
                member.setAdmin(j == 0);
                member.setJoinTime(System.currentTimeMillis());
                addGroupMember(member);
            }
            System.out.println("add 500 groupMember");
        }

    }
    public static void main(String[] args) {
        UserGroupDao dao = new UserGroupDao();
        dao.initTestData();
        dao.getGroupMembers(1000000000000000L,5,10).forEach(System.out::println);

    }

}

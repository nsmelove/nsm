package com.nsm.mvc.dao;

import com.google.common.collect.Lists;
import com.nsm.common.mongodb.MongodbUtil;
import com.nsm.common.utils.IdUtils;
import com.nsm.mvc.bean.GroupMember;
import com.nsm.mvc.bean.UserGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

    private void createIndex(){

    }

    public void addUserGroup(UserGroup group){
        template.insert(group);
    }

    public void addGroupMember(long groupId, GroupMember member){
        Query query = Query.query(Criteria.where("groupId").is(groupId));
        Update update = new Update().addToSet("members", member);
        template.updateFirst(query, update, UserGroup.class);
    }

    public UserGroup getUserGroup(long groupId){
        return template.findById(groupId, UserGroup.class);

    }

    public List<UserGroup> getAllUserGroup(int offset, int limit){
        Query query = new Query().skip(offset).limit(limit);
        return template.find(query, UserGroup.class);
    }



    public static void main(String[] args) {

        long userId =  1511275247177296L;
        long groupId = 1528990217785001L;
        UserGroupDao dao = new UserGroupDao();
        UserGroup group = new UserGroup();
        group.setGroupId(IdUtils.nextLong());
        group.setGroupName("学习组");
        group.setCreatorId(userId);
        group.setCreateTime(System.currentTimeMillis());
        GroupMember member = new GroupMember();
        member.setMemberId(userId);
        member.setAdmin(true);
        member.setJoinTime(System.currentTimeMillis());
        group.setMembers(Lists.newArrayList(member));
        dao.addUserGroup(group);
        dao.getAllUserGroup(0, 10).forEach(System.out::println);

    }

}

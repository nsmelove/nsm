package com.nsm.mvc.dao;

import com.google.common.collect.Lists;
import com.nsm.common.mongodb.MongodbUtil;
import com.nsm.mvc.bean.UserGroup;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Collection;
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

    public List<UserGroup> batchGetUserGroup(Collection<Long> groupIds){
        Query query = Query.query(Criteria.where("groupId").in(groupIds));
        return template.find(query, UserGroup.class);
    }

    public List<UserGroup> getUserGroups(int offset, int limit){
        Query query = new Query().skip(offset).limit(limit);
        return template.find(query, UserGroup.class);
    }

    public List<UserGroup> getUserGroups(long parGroupId, long creatorId, int offset, int limit){
        Criteria criteria = new Criteria();
        if(parGroupId > 0) {
            criteria = criteria.and("parGroupId").is(parGroupId);
        }
        if(creatorId > 0) {
            criteria = criteria.and("creatorId").is(creatorId);
        }
        Query query = new Query(criteria).skip(offset).limit(limit);
        return template.find(query, UserGroup.class);
    }

    public int countUserGroup(long parGroupId, long creatorId){
        List<AggregationOperation> aggOperations = Lists.newArrayListWithExpectedSize(3);
        if(parGroupId > 0) {
            aggOperations.add(Aggregation.match(Criteria.where("parGroupId").is(parGroupId)));
        }
        if(creatorId > 0) {
            aggOperations.add(Aggregation.match(Criteria.where("creatorId").is(creatorId)));
        }
        aggOperations.add(Aggregation.count().as("total"));
        Aggregation aggregation = Aggregation.newAggregation(aggOperations);
        List<Document> results = template.aggregate(aggregation, UserGroup.class, Document.class).getMappedResults();
        if(!results.isEmpty()) {
            return (int)results.get(0).get("total");
        }else {
            return 0;
        }
    }

    public void updateUserGroup(long groupId, UserGroup.Update updateParam) {
        Query query = new Query(Criteria.where("groupId").is(groupId));
        Update update = new Update();
        if(StringUtils.isNoneBlank(updateParam.groupName)) {
            update.set("groupName", updateParam.groupName);
        }
        if(updateParam.privacy != null) {
            update.set("privacy", updateParam.privacy);
        }
        if(updateParam.silent != null) {
            update.set("silent", updateParam.silent);
        }
        if(updateParam.delSubGIds != null && !updateParam.delSubGIds.isEmpty()) {
            update.pullAll("subGroupIds", updateParam.delSubGIds.toArray());
        }
        if(updateParam.addSubGIds != null && !updateParam.addSubGIds.isEmpty()) {
            update.addToSet("subGroupIds").each(updateParam.addSubGIds);
        }
        template.findAndModify(query, update, UserGroup.class);
    }


    public void deleteUserGroup(long groupId){
        Query query = new Query(Criteria.where("groupId").is(groupId));
        template.findAndRemove(query, UserGroup.class);
    }

    public void deleteUserGroups(long parGroupId, long creatorId){
        Criteria criteria = new Criteria();
        if(parGroupId > 0) {
            criteria = criteria.and("parGroupId").is(parGroupId);
        }
        if(creatorId > 0) {
            criteria = criteria.and("creatorId").is(creatorId);
        }
        Query query = new Query(criteria);
        template.findAllAndRemove(query, UserGroup.class);
    }

    public void batchDeleteUserGroup(Collection<Long> groupIds){
        Query query = Query.query(Criteria.where("groupId").in(groupIds));
        template.findAllAndRemove(query, UserGroup.class);
    }

    public static void main(String[] args) {
        UserGroupDao dao = new UserGroupDao();
        UserGroup.Update update = new UserGroup.Update();
        update.groupName = "天天爱学习";
        //update.delSubGIds = Lists.newArrayList(4L);
        update.addSubGIds = Lists.newArrayList(1L,2L,3L,4L);
        dao.updateUserGroup(1000000000000011L, update);
        System.out.println(dao.getUserGroup(1000000000000011L));

    }

}

package com.nsm.mvc.dao;

import com.google.common.collect.Lists;
import com.nsm.common.mongodb.MongodbUtil;
import com.nsm.mvc.bean.GroupMember;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/18.
 */
@Repository("groupMemberDao")
public class GroupMemberDao {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private MongoTemplate template = MongodbUtil.getTemplate();


    public void addGroupMember(GroupMember member){
        template.save(member);
    }

    public int batchAddGroupMember(List<GroupMember> members){
        BulkOperations operations = template.bulkOps(BulkOperations.BulkMode.ORDERED, GroupMember.class);
        return operations.insert(members).execute().getInsertedCount();
    }

    public int countGroupMember(long groupId, long memberId){
        List<AggregationOperation> aggOperations = Lists.newArrayListWithExpectedSize(2);
        if(groupId > 0) {
            aggOperations.add(Aggregation.match(Criteria.where("groupId").is(groupId)));
        }
        if(memberId > 0) {
            aggOperations.add(Aggregation.match(Criteria.where("memberId").is(memberId)));
        }
        aggOperations.add(Aggregation.count().as("total"));
        Aggregation aggregation = Aggregation.newAggregation(aggOperations);
        List<Document> results = template.aggregate(aggregation, GroupMember.class, Document.class).getMappedResults();
        if(!results.isEmpty()) {
            return (int)results.get(0).get("total");
        }else {
            return 0;
        }
    }

    public GroupMember getGroupMember(long groupId, long memberId) {
        Query query = Query.query(Criteria.where("groupId").is(groupId).and("memberId").is(memberId));
        return  template.findOne(query, GroupMember.class);
    }

    public List<GroupMember> getGroupMembers(long groupId, int offset, int limit) {
        Query query = Query.query(Criteria.where("groupId").is(groupId)).skip(offset).limit(limit);
        return  template.find(query, GroupMember.class);
    }

    public void updateGroupMember(long groupId, long memberId, Boolean isAdmin, Boolean silent){
        Query query = Query.query(Criteria.where("groupId").is(groupId).and("memberId").is(memberId));
        Update update = new Update();
        if(isAdmin != null) {
            update.set("isAdmin",isAdmin);
        }
        if(silent != null) {
            update.set("silent",silent);
        }
        template.findAndModify(query,update, GroupMember.class);

    }
    public GroupMember deleteGroupMember(long groupId, long memberId){
        Query query = Query.query(Criteria.where("groupId").is(groupId).and("memberId").is(memberId));
        return template.findAndRemove(query, GroupMember.class);
    }

    public void deleteGroupMembers(long groupId){
        Query query = Query.query(Criteria.where("groupId").is(groupId));
        template.findAndRemove(query, GroupMember.class);
    }
}

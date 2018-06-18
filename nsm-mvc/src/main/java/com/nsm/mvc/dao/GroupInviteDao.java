package com.nsm.mvc.dao;

import com.google.common.collect.Lists;
import com.nsm.common.mongodb.MongodbUtil;
import com.nsm.mvc.bean.GroupInvite;
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
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/18.
 */
@Repository("groupInviteDao")
public class GroupInviteDao {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private MongoTemplate template = MongodbUtil.getTemplate();

    public void addGroupInvite(GroupInvite invite){
        template.save(invite);
    }

    public int batchAddGroupInvite(List<GroupInvite> invites){
        BulkOperations operations = template.bulkOps(BulkOperations.BulkMode.ORDERED, GroupInvite.class);
        return operations.insert(invites).execute().getInsertedCount();
    }

    public int countGroupGroupInvite(long groupId, long inviteeId){
        List<AggregationOperation> aggOperations = Lists.newArrayListWithExpectedSize(2);
        if(groupId > 0) {
            aggOperations.add(Aggregation.match(Criteria.where("groupId").is(groupId)));
        }
        if(inviteeId > 0) {
            aggOperations.add(Aggregation.match(Criteria.where("inviteeId").is(inviteeId)));
        }
        aggOperations.add(Aggregation.count().as("total"));
        Aggregation aggregation = Aggregation.newAggregation(aggOperations);
        List<Document> results = template.aggregate(aggregation, GroupInvite.class, Document.class).getMappedResults();
        if(!results.isEmpty()) {
            return (int)results.get(0).get("total");
        }else {
            return 0;
        }
    }

    public GroupInvite getGroupInvite(long groupId, long inviteeId) {
        Query query = Query.query(Criteria.where("groupId").is(groupId).and("inviteeId").is(inviteeId));
        return  template.findOne(query, GroupInvite.class);
    }

    public List<GroupInvite> getGroupInvites(long groupId, long inviteeId, int offset, int limit) {
        Criteria criteria = new Criteria();
        if(groupId > 0) {
            criteria.and("groupId").is(groupId);
        }
        if(inviteeId > 0) {
            criteria.and("inviteeId").is(inviteeId);
        }
        Query query = Query.query(criteria).skip(offset).limit(limit);
        return  template.find(query, GroupInvite.class);
    }


    public GroupInvite deleteGroupInvite(long groupId, long inviteeId){
        Query query = Query.query(Criteria.where("groupId").is(groupId).and("inviteeId").is(inviteeId));
        return template.findAndRemove(query, GroupInvite.class);
    }

    public void deleteGroupGroupInvites(long groupId, long inviteeId){
        Criteria criteria = new Criteria();
        if(groupId > 0) {
            criteria.and("groupId").is(groupId);
        }
        if(inviteeId > 0) {
            criteria.and("inviteeId").is(inviteeId);
        }
        Query query = Query.query(criteria);
        template.findAllAndRemove(query, GroupMember.class);
    }
}

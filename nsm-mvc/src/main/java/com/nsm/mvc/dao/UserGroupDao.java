package com.nsm.mvc.dao;

import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.nsm.common.mongodb.MongodbUtil;
import com.nsm.common.utils.BeanUtils;
import com.nsm.common.utils.IdUtils;
import com.nsm.mvc.bean.UserGroup;
import org.apache.commons.beanutils.BeanMap;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/13.
 */
@Repository("userGroupDao")
public class UserGroupDao {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String COLLECTION_NAME = "userGroup";

    private MongoDatabase mongoDatabase = MongodbUtil.getDataBase();

    private void createIndex(){
        MongoCollection<Document> collection = mongoDatabase.getCollection(COLLECTION_NAME);
        //collection.createIndex()
    }

    public boolean addUserGroup(UserGroup group){

        Document document = new Document();
        document.put("_id", group.getGroupId());
        document.putAll(BeanUtils.beanToMap(group));
        //document.
        MongoCollection<Document> collection = mongoDatabase.getCollection(COLLECTION_NAME);
        collection.insertOne(document);
        return true;
    }

    public void addGroupMember(long member){

    }

    public Document getUserGroup(long groupId){
        MongoCollection<Document> collection = mongoDatabase.getCollection(COLLECTION_NAME);
        return collection.find(Filters.eq("groupId",groupId)).first();
    }

    public List<Document> getAllUserGroup(int offset, int limit){
        MongoCollection<Document> collection = mongoDatabase.getCollection(COLLECTION_NAME);
        FindIterable<Document> documents = collection.find().skip(offset).limit(limit);
        return Lists.newArrayList(documents.iterator());
    }



    public static void main(String[] args) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        long userId = 1527524717296L;
        UserGroupDao dao = new UserGroupDao();
        UserGroup group = new UserGroup();
        group.setGroupId(IdUtils.nextLong());
        group.setGroupName("讨论组");
        group.setCreatorId(userId);
        group.setCreateTime(System.currentTimeMillis());
        Map<Object, Object> map = new BeanMap(group);
        map.forEach((k,v) ->{
            System.out.println(k + " type is " + k.getClass());
        });
               //dao.addUserGroup(group);
        dao.getAllUserGroup(0, 10).forEach(document -> {
            System.out.println(document);
        });
//        System.out.println(dao.getUserGroup(627262631915201L));

    }

}

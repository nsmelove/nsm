package com.nsm.core.dao;

import com.nsm.common.mongodb.MongodbUtil;
import com.nsm.core.entity.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by nieshuming on 2018/7/3
 */
@Repository("contactDao")
public class ContactDao {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private MongoTemplate template = MongodbUtil.getTemplate();

    public void addContact(Contact contact){
        template.save(contact);
    }

    public List<Contact> getContactsByUserFromOrTo(long userId, int offset, int limit){
        Criteria fromIsUser = Criteria.where("userFrom").is(userId);
        Criteria toIsUser = Criteria.where("userTo").is(userId);
        Query query = Query.query(new Criteria().orOperator(fromIsUser, toIsUser));
        query.skip(offset).limit(limit);
        return template.find(query, Contact.class);
    }

    public Contact getContactByUserFromAndTo(long userId, long targetId){
        Criteria userIsFrom = Criteria.where("userFrom").is(userId).and("userTo").is(targetId);
        Criteria targetIsFrom = Criteria.where("userFrom").is(targetId).and("userTo").is(userId);
        Query query = Query.query(new Criteria().orOperator(userIsFrom, targetIsFrom));
        return template.findOne(query, Contact.class);
    }

    public boolean delContactByUserFromAndTo(long userId, long targetId){
        Criteria userIsFrom = Criteria.where("userFrom").is(userId).and("userTo").is(targetId);
        Criteria targetIsFrom = Criteria.where("userFrom").is(targetId).and("userTo").is(userId);
        Query query = Query.query(new Criteria().orOperator(userIsFrom, targetIsFrom));
        return template.remove(query, Contact.class).getDeletedCount() > 0;
    }
}

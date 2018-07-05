package com.nsm.core.service;

import com.nsm.core.SpringContainer;
import com.nsm.core.pojo.ContactInfo;

import java.util.List;
import java.util.Set;

/**
 * Created by nieshuming on 2018/7/5
 */
public class ContactServiceTest {

    public static void main(String[] args) {
        ContactService contactService = SpringContainer.getBean(ContactService.class);
        long userId = 1527524717296L;
        Set<Long> contactIds =  contactService.getContactSet(userId);
        contactIds.forEach(System.out::println);
        List<ContactInfo> contactInfos = contactService.getContacts(userId, 0, 20);
        contactInfos.forEach(System.out::println);
        System.exit(0);
    }
}

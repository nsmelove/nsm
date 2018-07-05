package com.nsm.mvc.controller;

import com.nsm.core.pojo.ContactInfo;
import com.nsm.core.service.ContactService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by nieshuming on 2018/7/3
 */
@RestControllerAdvice
@RequestMapping("/contact")
public class ContactController extends ErrorHandler{

    @Resource
    private ContactService contactService;

    @RequestMapping("/{contactId}/request")
    public void reqContact(@RequestAttribute long uid, @PathVariable long contactId){
        contactService.reqContact(uid, contactId);
    }

    @RequestMapping("/request/list")
    @ResponseBody
    public List<ContactInfo> contactReqList(@RequestAttribute long uid){
        return contactService.contactReqList(uid);
    }

    @RequestMapping("/receive/list")
    @ResponseBody
    public List<ContactInfo> contactRecReqList(@RequestAttribute long uid){
        return contactService.contactRecReqList(uid);
    }

    @RequestMapping("/{contactId}/accept")
    public void acceptContact(@RequestAttribute long uid, @PathVariable long contactId){
        contactService.acceptContact(uid, contactId);
    }

    @RequestMapping("/{contactId}/reject")
    public void rejectContact(@RequestAttribute long uid, @PathVariable long contactId){
        contactService.rejectContact(uid, contactId);
    }

    @RequestMapping("/list")
    @ResponseBody
    public List<ContactInfo> contactList(@RequestAttribute long uid,
                            @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        offset = offset == null ? 0 : offset;
        limit = limit == null ? 20 : limit;
        return contactService.getContacts(uid, offset, limit);
    }

    @RequestMapping("/{contactId}/remove")
    public void removeContact(@RequestAttribute long uid, @PathVariable long contactId){
        contactService.removeContact(uid, contactId);
    }
}

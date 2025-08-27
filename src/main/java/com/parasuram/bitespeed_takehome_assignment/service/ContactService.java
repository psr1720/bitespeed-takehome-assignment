package com.parasuram.bitespeed_takehome_assignment.service;

import com.parasuram.bitespeed_takehome_assignment.entity.Contact;
import com.parasuram.bitespeed_takehome_assignment.enums.LinkPrecedence;
import com.parasuram.bitespeed_takehome_assignment.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {
    @Autowired
    ContactRepository contactRepository;
    public void identity(String emailId, String phoneNo){
        //TODO - check if the user already exists.
        List<Contact> primaryMatches = contactRepository.findPrimaryContactsByEmailOrPhone(emailId, phoneNo);
        if (primaryMatches.isEmpty()){
            insertPrimaryContact(emailId, phoneNo);
        }
    }
    public void insertPrimaryContact(String emailId, String phoneNo){
        Contact newContact = new Contact();
        if (!emailId.isBlank()) {
            newContact.setEmailId(emailId);
        }
        if (!phoneNo.isBlank()) {
            newContact.setPhoneNumber(phoneNo);
        }
        newContact.setLinkPrecedence(LinkPrecedence.PRIMARY);
        contactRepository.save(newContact);
    }
}

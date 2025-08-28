package com.parasuram.bitespeed_takehome_assignment.service;

import com.parasuram.bitespeed_takehome_assignment.DTO.ContactDTO;
import com.parasuram.bitespeed_takehome_assignment.entity.Contact;
import com.parasuram.bitespeed_takehome_assignment.enums.LinkPrecedence;
import com.parasuram.bitespeed_takehome_assignment.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ContactService {
    @Autowired
    ContactRepository contactRepository;

    @Transactional
    public ContactDTO identify(String emailId, String phoneNo){

        List<Contact> primaryMatches = contactRepository.findPrimaryContactsByEmailOrPhone(emailId, phoneNo);
        List<Contact> secondaryMatches = contactRepository.findSecondaryContactsByEmailOrPhone(emailId, phoneNo);
        if (emailId != null && emailId.isBlank()) emailId = null;
        if (phoneNo != null && phoneNo.isBlank()) phoneNo = null;

        // When there are no matching records at all a new primary record needs to be inserted.
        if (primaryMatches.isEmpty() && secondaryMatches.isEmpty()){
            return insertPrimaryContact(emailId, phoneNo);
        }
        // When there is a match in either primary or secondary but new info also exists we have to insert a secondary contact.
        // This function also makes sure that if there is an exact match it considers that there is no new value and the record doesn't get inserted
        if (newInfoExists(emailId, phoneNo, primaryMatches, secondaryMatches)) {
            // Since the case with multiple primaries was already handled if we have exactly one primary we will insert a secondary contact
            return insertSecondaryContact(emailId, phoneNo, primaryMatches, secondaryMatches);
        }
        // If no new insertion is being made then it is possible that we have to convert some records
        // Note - There is no possible scenario where if a record had to inserted then a primary has
        //        to be changed to a secondary.
        //  There are 3 scenarios for a primary to be converted to a secondary
        //  1. more than 1 primary match exists.
        //  2. a primary match exists and a secondary match's link id is not the same as the primary match's id
        //  3. more than 1 secondary match exists and their link ids are not the samet
        //  To solve this get all the primaries for the given matches and sort them by createdAt to get
        //  the first primary and convert all the other primaries to secondary and cascade that effect.
        Contact currentPrimary = convertPrimariesIfNeeded(primaryMatches, secondaryMatches);
        if (currentPrimary != null){
            return responseBuilder(currentPrimary);
        }
        // if none of the above scenarios were hit then it just means we have a partial or a full match with no
        // new data and no need of a merge so simple build the response by finding out the primary contact and
        // it's secondary using the responseBuilder and return.
        Contact primaryContact = getPrimaryContact(primaryMatches, secondaryMatches);
        return responseBuilder(primaryContact);
    }

    // helper function to insert into db.
    private Contact insertContact(String emailId, String phoneNo, boolean isPrimary, int linkedId){
        Contact newContact = new Contact();
        newContact.setEmailId(emailId);
        newContact.setPhoneNumber(phoneNo);
        if(!isPrimary) {
            newContact.setLinkPrecedence(LinkPrecedence.SECONDARY);
            newContact.setLinkedId(linkedId);
        }
        else {
            newContact.setLinkPrecedence(LinkPrecedence.PRIMARY);
        }
        return contactRepository.save(newContact);
    }

    private ContactDTO insertPrimaryContact(String emailId, String phoneNo){
        Contact primaryContact = insertContact(emailId, phoneNo, true, 0); // insert the primary contact
        return responseBuilder(primaryContact);
    }

    private ContactDTO insertSecondaryContact(String emailId, String phoneNo, List<Contact> primaryMatches, List<Contact> secondaryMatches){
        Contact primaryContact = null;
        if (!primaryMatches.isEmpty()) {
            primaryContact = primaryMatches.getFirst();
            insertContact(emailId, phoneNo, false, primaryContact.getId()); // insert the secondary contact
        }
        else{
            insertContact(emailId, phoneNo, false, secondaryMatches.getFirst().getLinkedId());
            Optional<Contact> pContact = contactRepository.findById(secondaryMatches.getFirst().getLinkedId());
            if (pContact.isPresent()) primaryContact = pContact.get();
        }
        return responseBuilder(primaryContact);
    }

    // helper function to check if the income request has any new information that isn't in the db
    private boolean newInfoExists(String email, String phone, List<Contact> primaryMatches, List<Contact> secondaryMatches) {
        boolean emailMatched = (email == null);
        boolean phoneMatched = (phone == null);

        for (Contact contact : primaryMatches) {
            if (email != null && email.equals(contact.getEmailId())) emailMatched = true;
            if (phone != null && phone.equals(contact.getPhoneNumber())) phoneMatched = true;
        }

        for (Contact contact : secondaryMatches) {
            if (email != null && email.equals(contact.getEmailId())) emailMatched = true;
            if (phone != null && phone.equals(contact.getPhoneNumber())) phoneMatched = true;
        }
        return (emailMatched ^ phoneMatched);
    }

    // finds all primary contacts for the request parameters' matches and set all later inserted primaries to secondary and cascades the effect
    private Contact convertPrimariesIfNeeded(List<Contact> primaryMatches, List<Contact> secondaryMatches) {
        Set<Contact> allPrimaries = new HashSet<>(primaryMatches);

        for (Contact secondary : secondaryMatches) {
            Contact linkedPrimary = contactRepository.findById(secondary.getLinkedId()).orElse(null);
            if (linkedPrimary != null) {
                allPrimaries.add(linkedPrimary);
            }
        }

        List<Contact> sortedPrimaries = new ArrayList<>(allPrimaries);
        if (sortedPrimaries.size() > 1) {
            sortedPrimaries.sort(Comparator.comparing(Contact::getCreatedAt));

            Contact mainPrimary = sortedPrimaries.get(0); // The actual primary

            for (int i = 1; i < sortedPrimaries.size(); i++) {
                Contact toDemote = sortedPrimaries.get(i);
                updateToSecondary(toDemote, mainPrimary.getId());

                // Cascade to the new secondary's secondaries
                List<Contact> secondariesToUpdate = contactRepository.findAllByLinkedId(toDemote.getId());
                for (Contact sec : secondariesToUpdate) {
                    updateToSecondary(sec, mainPrimary.getId());
                }
            }
            return mainPrimary;
        }

        if (!sortedPrimaries.isEmpty()) {
            return sortedPrimaries.get(0);
        }

        return null;
    }

    // helper function to change the status of a primary contact.
    private void updateToSecondary(Contact contact, int newLinkId) {
        contact.setLinkPrecedence(LinkPrecedence.SECONDARY);
        contact.setLinkedId(newLinkId);
        contactRepository.save(contact);
    }

    // helper function to find the primary contact of the give request parameters
    private Contact getPrimaryContact(List<Contact> primaryMatches, List<Contact> secondaryMatches) {
        if (!primaryMatches.isEmpty()){
            return primaryMatches.getFirst();
        }
        else {
            int primaryContactID = secondaryMatches.getFirst().getLinkedId();
            Optional<Contact> contact = contactRepository.findById(primaryContactID);
            if (contact.isPresent()){
                return contact.get();
            }
        }
        return new Contact();
    }

    // helper function to build a response and return all unique emails and phoneNos for the give request.
    private ContactDTO responseBuilder(Contact primaryContact) {
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setPrimaryContactId(primaryContact.getId());
        // LinkedHashSet because it preserves order while not allowing duplicates
        Set<String> emailSet = new LinkedHashSet<>();
        Set<String> phoneSet = new LinkedHashSet<>();
        List<Integer> secondaryContactIds = new ArrayList<>();
        // Primary contact values as first value of the list
        if (primaryContact.getEmailId() != null) {
            emailSet.add(primaryContact.getEmailId());
        }
        if (primaryContact.getPhoneNumber() != null) {
            phoneSet.add(primaryContact.getPhoneNumber());
        }
        // Getting all secondary contacts
        List<Contact> secondaryContacts = contactRepository.findAllByLinkedId(primaryContact.getId());
        for (Contact contact : secondaryContacts) {
            if (contact.getEmailId() != null) {
                emailSet.add(contact.getEmailId());
            }
            if (contact.getPhoneNumber() != null) {
                phoneSet.add(contact.getPhoneNumber());
            }
            secondaryContactIds.add(contact.getId());
        }
        contactDTO.setEmails(new ArrayList<>(emailSet));
        contactDTO.setPhoneNumbers(new ArrayList<>(phoneSet));
        contactDTO.setSecondaryContactIds(secondaryContactIds);

        return contactDTO;
    }

}

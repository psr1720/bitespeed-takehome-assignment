package com.parasuram.bitespeed_takehome_assignment.controller;

import com.parasuram.bitespeed_takehome_assignment.DTO.ContactDTO;
import com.parasuram.bitespeed_takehome_assignment.DTO.IdentifyRequestDTO;
import com.parasuram.bitespeed_takehome_assignment.DTO.IdentityResponseDTO;
import com.parasuram.bitespeed_takehome_assignment.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactController {
    @Autowired
    ContactService contactService;

    @PostMapping("/identify")
    public ResponseEntity<IdentityResponseDTO> identity(@Validated @RequestBody IdentifyRequestDTO requestDTO) {
        String email = requestDTO.getEmailId();
        String phoneNo = requestDTO.getPhoneNumber();
        if(email.isBlank() && phoneNo.isBlank()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ContactDTO contactDTO= contactService.identify(email, phoneNo);
        IdentityResponseDTO responseDTO = new IdentityResponseDTO();
        responseDTO.setContactDTO(contactDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        //TODO - Return appropriate response if both email and phNo are missing
        //TODO - Implement actual logic to insert and update in the service layer and persist with repo layer
    }
}

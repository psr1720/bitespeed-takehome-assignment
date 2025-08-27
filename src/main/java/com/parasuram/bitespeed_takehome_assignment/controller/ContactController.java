package com.parasuram.bitespeed_takehome_assignment.controller;

import com.parasuram.bitespeed_takehome_assignment.DTO.Contact;
import com.parasuram.bitespeed_takehome_assignment.DTO.IdentifyRequestDTO;
import com.parasuram.bitespeed_takehome_assignment.DTO.IdentityResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactController {

    @PostMapping("/identify")
    public ResponseEntity<IdentityResponseDTO> identity(@Validated @RequestBody IdentifyRequestDTO requestDTO) {
        IdentityResponseDTO responseDTO = new IdentityResponseDTO();
        Contact contact = new Contact();
        responseDTO.setContact(contact);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        //TODO - Return appropriate response if both email and phNo are missing
        //TODO - Implement actual logic to insert and update in the service layer and persist with repo layer
    }
}

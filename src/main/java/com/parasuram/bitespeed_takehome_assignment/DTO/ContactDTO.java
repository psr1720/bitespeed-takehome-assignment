package com.parasuram.bitespeed_takehome_assignment.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ContactDTO {
    private Integer primaryContactId;
    private List<String> emails = new ArrayList<>();
    private List<String> phoneNumbers = new ArrayList<>();
    private List<Integer> secondaryContactIds = new ArrayList<>();
}

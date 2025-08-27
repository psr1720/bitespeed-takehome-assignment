package com.parasuram.bitespeed_takehome_assignment.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IdentifyRequestDTO {
    private String emailId;
    private String phoneNumber;
}

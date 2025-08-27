package com.parasuram.bitespeed_takehome_assignment.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IdentityResponseDTO {
    @JsonProperty("contact")
    private ContactDTO contactDTO;
}

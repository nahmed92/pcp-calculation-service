package com.deltadental.mtv.sync.interservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Person {
    private List<Address> address;
    private List<ContactPerson> contactPerson;
    private List<Email> email;
    private String genderCode;
    private String maritalStatusCode;
    private String personBirthDate;
    private String personFirstName;
    private String personGenderCode;
    private String personIdentfier;
    private String personLastName;
    private String personMiddleName;
    private List<Phone> phone;
    private String socialSecurityNumber;
    private String suffix;
}

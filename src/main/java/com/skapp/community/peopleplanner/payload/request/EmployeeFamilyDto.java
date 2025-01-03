package com.skapp.community.peopleplanner.payload.request;

import com.skapp.community.peopleplanner.type.FamilyRelationship;
import com.skapp.community.peopleplanner.type.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeFamilyDto {

	private Long familyId;

	private String firstName;

	private String lastName;

	private String parentName;

	private Gender gender;

	private LocalDate birthDate;

	private FamilyRelationship familyRelationship;

}

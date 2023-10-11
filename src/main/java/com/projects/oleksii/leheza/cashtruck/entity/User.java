package com.projects.oleksii.leheza.cashtruck.entity;

import com.projects.oleksii.leheza.cashtruck.enums.UserRole;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class User {

	private Long id;
	private String firstname;
	private String lastname;
	private UserRole role;
}

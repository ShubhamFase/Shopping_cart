package com.ecom.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.ecom.model.UserDetails1;

public interface UserService {

	public UserDetails1 saveUser(UserDetails1 userDetails);
	
	public UserDetails1 getUserByEmail(String email);
	
	public List<UserDetails1> getUsers(String role);

	public boolean updateAccountStatus(int id, boolean status);
	
}

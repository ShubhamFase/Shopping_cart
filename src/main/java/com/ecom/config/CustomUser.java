package com.ecom.config;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ecom.model.UserDetails1;

public class CustomUser implements UserDetails {

    private UserDetails1 user;	

	public CustomUser(UserDetails1 user) {
		super();
		this.user = user;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
	  SimpleGrantedAuthority authority=new SimpleGrantedAuthority(user.getRole());
		return Arrays.asList(authority);
	}

	@Override
	public String getPassword() {
		
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		
		return user.getEmail();
	}

}

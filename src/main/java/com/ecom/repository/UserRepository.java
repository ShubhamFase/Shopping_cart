package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.ecom.model.UserDetails1;

@Repository
public interface UserRepository extends JpaRepository<UserDetails1, Integer> {

	public UserDetails1 findByEmail(String email);

	public List<UserDetails1> findByRole(String role);
	
	public UserDetails1 findByResetToken(String token);
}

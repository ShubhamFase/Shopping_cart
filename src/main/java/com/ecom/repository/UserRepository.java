package com.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.model.UserDetails1;

@Repository
public interface UserRepository extends JpaRepository<UserDetails1, Integer> {

	public UserDetails1 findByEmail(String email);
}

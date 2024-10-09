package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

	public Cart findByProductIdAndUserId(int pid, int uid);
	
	public Integer countByUserId(int userId);
	
	public List<Cart> findByUserId(int userId);
	
}

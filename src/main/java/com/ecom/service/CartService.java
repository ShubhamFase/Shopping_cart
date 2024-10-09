package com.ecom.service;

import java.util.List;

import com.ecom.model.Cart;

public interface CartService {

	public Cart saveCart(int pid,int uid); 
	
	public List<Cart> getCartByUser(int userId);
	
	public Integer getCountCart(int userID);
	
	public void updateQuantity(String sy,int cid);
}
package com.ecom.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;

import jakarta.mail.MessagingException;


public interface OrderService {

	public void saveOrder(int userId,OrderRequest orderRequest) throws UnsupportedEncodingException, MessagingException;

	public List<ProductOrder> getOrderByUser(int userId);
	
	public ProductOrder orderStatus(int id,String st);
	
	public List<ProductOrder> getAllOrders();
	
	public ProductOrder getOrderByOrderId(String orderId);
}	
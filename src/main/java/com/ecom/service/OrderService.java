package com.ecom.service;

import java.util.List;

import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;


public interface OrderService {

	public void saveOrder(int userId,OrderRequest orderRequest);

	public List<ProductOrder> getOrderByUser(int userId);
	
	public Boolean orderStatus(int id,String st);
}	
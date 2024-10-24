package com.ecom.service.impl;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.model.Cart;
import com.ecom.model.OrderAddress;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.repository.CartRepository;
import com.ecom.repository.OrderProductRepository;
import com.ecom.service.OrderService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.mail.MessagingException;

@Service 
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	private OrderProductRepository orderProductRepository;
	
	@Autowired 
	private CartRepository cartRepository;
	
	@Autowired
	private CommonUtil commonUtil;


	@Override
	public void saveOrder(int userId, OrderRequest orderRequest) throws UnsupportedEncodingException, MessagingException {
		
		List<Cart> cart = cartRepository.findByUserId(userId);
		
		for(Cart carts:cart) 
		{
			ProductOrder order=new ProductOrder();
			order.setOrderId(UUID.randomUUID().toString());
			order.setOrderDate(LocalDate.now());
			
			order.setProduct(carts.getProduct());;
			order.setPrice(carts.getProduct().getDiscountPrice());
			
			order.setQuantity(carts.getQuantity());
			order.setUser(carts.getUser());
			
			order.setStatus(OrderStatus.IN_PROGRESS.getName());
			order.setPaymentType(orderRequest.getPaymentType());
			
			OrderAddress orderAddress=new OrderAddress();
			orderAddress.setFirstName(orderRequest.getFirstName());
			orderAddress.setLastName(orderRequest.getLastName());
			orderAddress.setEmail(orderRequest.getEmail());
			orderAddress.setMobileNumber(orderRequest.getMobileNumber());
			orderAddress.setAddress(orderRequest.getAddress());
			orderAddress.setCity(orderRequest.getCity());
			orderAddress.setState(orderRequest.getState());
			orderAddress.setPincode(orderRequest.getPincode());
			
			order.setOrderAddress(orderAddress);
			ProductOrder saveOrder = orderProductRepository.save(order);	
			commonUtil.sendMailForProductOrder(saveOrder, "success");
		}

	}

	@Override
	public List<ProductOrder> getOrderByUser(int userId) 
	{
		List<ProductOrder> orders=orderProductRepository.findByUserId(userId);
		return orders;
	}

	@Override
	public ProductOrder orderStatus(int id, String st) {
		Optional<ProductOrder> findById = orderProductRepository.findById(id);
		
		if(findById.isPresent()) 
		{
			ProductOrder productOrder = findById.get();
			productOrder.setStatus(st);
			ProductOrder updateOrder = orderProductRepository.save(productOrder);
			return updateOrder;
		}
		return null;
	}

	@Override
	public List<ProductOrder> getAllOrders() {
		
		return orderProductRepository.findAll();
	}

	@Override
	public ProductOrder getOrderByOrderId(String orderId) {
		return orderProductRepository.findByOrderId(orderId);
		 
	}

	
	
}

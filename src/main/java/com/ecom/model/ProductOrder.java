package com.ecom.model;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class ProductOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String orderId;
	
	private LocalDate orderDate;
	
	@ManyToOne
	private Product product;
	
	private Double price;
	
	private int quantity;
	
	@ManyToOne
	private UserDetails1 user;
	
	private String status;
	
	private String paymentType;
	
	@OneToOne(cascade = CascadeType.ALL)
	private OrderAddress orderAddress;

	public ProductOrder() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProductOrder(int id, String orderId, LocalDate orderDate, Product product, Double price, int quantity,
			UserDetails1 user, String status, String paymentType, OrderAddress orderAddress) {
		super();
		this.id = id;
		this.orderId = orderId;
		this.orderDate = orderDate;
		this.product = product;
		this.price = price;
		this.quantity = quantity;
		this.user = user;
		this.status = status;
		this.paymentType = paymentType;
		this.orderAddress = orderAddress;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public LocalDate getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public UserDetails1 getUser() {
		return user;
	}

	public void setUser(UserDetails1 user) {
		this.user = user;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public OrderAddress getOrderAddress() {
		return orderAddress;
	}

	public void setOrderAddress(OrderAddress orderAddress) {
		this.orderAddress = orderAddress;
	} 

	
}
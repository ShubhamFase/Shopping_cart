package com.ecom.model;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@ManyToOne
	private UserDetails1 user;
	@ManyToOne
	private Product product;
	private int quantity;
	@Transient
	private double totalPrice;
	
	@Transient
	private double totalOrderPrice;
	public Cart() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Cart(int id, UserDetails1 user, Product product, int quantity, double totalPrice,double totalOrderPrice) {
		super();
		this.id = id;
		this.user = user;
		this.product = product;
		this.quantity = quantity;
		this.totalPrice = totalPrice;
		this.totalOrderPrice=totalOrderPrice;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public UserDetails1 getUser() {
		return user;
	}
	public void setUser(UserDetails1 user) {
		this.user = user;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public double getTotalOrderPrice() {
		return totalOrderPrice;
	}
	public void setTotalOrderPrice(double totalOrderPrice) {
		this.totalOrderPrice = totalOrderPrice;
	}
	
	
}

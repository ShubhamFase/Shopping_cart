package com.ecom.service;

import java.util.List;

import com.ecom.model.Category;
import com.ecom.model.Product;

public interface ProductService {

	public Product saveProduct(Product product);
	
	public List<Product> getAllProducts(); 
	
	public Boolean deleteProduct(Integer id);
	
	public Product getProductById(Integer id);
	
	public List<Product> getAllActiveProduct(String category);
	
	public List<Product> searchProduct(String ch);
	
}

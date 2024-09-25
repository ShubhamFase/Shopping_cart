package com.ecom.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Category;
import com.ecom.repository.CategoryRepository;
import com.ecom.service.CategoryService;

@Service
public  class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	
	public Category saveCategory(Category category) 
	{
		
		return categoryRepository.save(category);	
		
	}

	
	public List<Category> getAllCategory() 
	{
		
		return categoryRepository.findAll();
	}


	@Override
	public boolean existCategory(String name) {
		
		return categoryRepository.existsByName(name);
	}


	@Override
	public Boolean deleteCategory(long id) {
		
		Category category=categoryRepository.findById(id).orElse(null);
		
		if(!ObjectUtils.isEmpty(category)) 
		{
			categoryRepository.delete(category);		
		   return true;
		}   
		return false;
	}

	public Category getCategoryById(long id) {
	
		Category category=categoryRepository.findById(id).orElse(null);
		return category;
	}


	@Override
	public List<Category> getAllActiveCategory() {
		List<Category> categories = categoryRepository.findByIsActiveTrue();
		return categories;
	}


	


	

}

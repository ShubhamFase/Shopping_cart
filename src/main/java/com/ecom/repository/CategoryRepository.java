package com.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecom.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
   public boolean existsByName(String name);
}


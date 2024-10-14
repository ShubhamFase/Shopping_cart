package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.model.ProductOrder;

@Repository
public interface OrderProductRepository extends JpaRepository<ProductOrder, Integer> {

	List<ProductOrder> findByUserId(int userId);

}

package com.example.hotwax.ecom.repository;


import com.example.hotwax.ecom.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}

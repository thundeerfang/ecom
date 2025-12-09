package com.example.hotwax.ecom.repository;
import com.example.hotwax.ecom.entity.Customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}

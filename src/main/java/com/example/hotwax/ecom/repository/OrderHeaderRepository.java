package com.example.hotwax.ecom.repository;


import com.example.hotwax.ecom.entity.OrderHeader;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Integer> {
}

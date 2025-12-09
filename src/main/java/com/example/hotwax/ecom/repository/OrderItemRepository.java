package com.example.hotwax.ecom.repository;

import com.example.hotwax.ecom.entity.OrderItem;
import com.example.hotwax.ecom.entity.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {

    List<OrderItem> findByOrderId(Integer orderId);
}


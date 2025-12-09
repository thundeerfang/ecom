package com.example.hotwax.ecom.controller;

import com.example.hotwax.ecom.entity.OrderHeader;
import com.example.hotwax.ecom.entity.OrderItem;
import com.example.hotwax.ecom.service.EcomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class EcomController {

    private final EcomService orderService;

    public EcomController(EcomService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody OrderHeader request) {
        try {
            Map<String, Object> body = orderService.createOrder(request);
            return ResponseEntity.status(201).body(body);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Integer orderId) {
        try {
            Map<String, Object> body = orderService.getOrder(orderId);
            return ResponseEntity.ok(body);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/orders/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable Integer orderId,
                                         @RequestBody OrderHeader request) {
        try {
            return ResponseEntity.ok(orderService.updateOrder(orderId, request));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Integer orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/orders/{orderId}/items")
    public ResponseEntity<?> addItem(@PathVariable Integer orderId,
                                     @RequestBody OrderItem request) {
        try {
            return ResponseEntity.status(201).body(orderService.addItem(orderId, request));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/orders/{orderId}/items/{orderItemSeqId}")
    public ResponseEntity<?> updateItem(@PathVariable Integer orderId,
                                        @PathVariable Integer orderItemSeqId,
                                        @RequestBody OrderItem request) {
        try {
            return ResponseEntity.ok(orderService.updateItem(orderId, orderItemSeqId, request));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/orders/{orderId}/items/{orderItemSeqId}")
    public ResponseEntity<?> deleteItem(@PathVariable Integer orderId,
                                        @PathVariable Integer orderItemSeqId) {
        try {
            orderService.deleteItem(orderId, orderItemSeqId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}

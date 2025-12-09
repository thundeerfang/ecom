package com.example.hotwax.ecom.service;

import com.example.hotwax.ecom.entity.*;
import com.example.hotwax.ecom.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EcomService {

    private final OrderHeaderRepository orderHeaderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ContactMechRepository contactMechRepository;
    private final ProductRepository productRepository;

    public EcomService(OrderHeaderRepository orderHeaderRepository,
                        OrderItemRepository orderItemRepository,
                        CustomerRepository customerRepository,
                        ContactMechRepository contactMechRepository,
                        ProductRepository productRepository) {
        this.orderHeaderRepository = orderHeaderRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerRepository = customerRepository;
        this.contactMechRepository = contactMechRepository;
        this.productRepository = productRepository;
    }

    // -------- CREATE ORDER --------
    public Map<String, Object> createOrder(OrderHeader request) {
        if (request.getCustomerId() == null
                || request.getOrderDate() == null
                || request.getShippingContactMechId() == null
                || request.getBillingContactMechId() == null) {
            throw new IllegalArgumentException("Missing required fields");
        }

        Customer customer = customerRepository.findById(request.getCustomerId()).orElse(null);
        if (customer == null) {
            throw new NoSuchElementException("Customer not found");
        }

        ContactMech shippingContact = contactMechRepository.findById(request.getShippingContactMechId()).orElse(null);
        ContactMech billingContact = contactMechRepository.findById(request.getBillingContactMechId()).orElse(null);
        if (shippingContact == null || billingContact == null) {
            throw new IllegalArgumentException("Shipping or billing contact not found");
        }

        if (!Objects.equals(shippingContact.getCustomerId(), request.getCustomerId())
                || !Objects.equals(billingContact.getCustomerId(), request.getCustomerId())) {
            throw new IllegalArgumentException("Contacts do not belong to the given customer");
        }

        List<OrderItem> items = request.getItems();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("At least one order item is required");
        }

        for (OrderItem itemReq : items) {
            if (itemReq.getProductId() == null || itemReq.getQuantity() == null) {
                throw new IllegalArgumentException("Each item must have productId and quantity");
            }
            if (itemReq.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero");
            }
            if (productRepository.findById(itemReq.getProductId()).isEmpty()) {
                throw new IllegalArgumentException("Invalid productId: " + itemReq.getProductId());
            }
        }

        // Save order header
        OrderHeader header = new OrderHeader();
        header.setOrderDate(request.getOrderDate());
        header.setCustomerId(request.getCustomerId());
        header.setShippingContactMechId(request.getShippingContactMechId());
        header.setBillingContactMechId(request.getBillingContactMechId());

        OrderHeader savedHeader = orderHeaderRepository.save(header);

        // Save items
        int seq = 1;
        for (OrderItem itemReq : items) {
            OrderItem item = new OrderItem();
            item.setOrderId(savedHeader.getOrderId());
            item.setOrderItemSeqId(seq++);
            item.setProductId(itemReq.getProductId());
            item.setQuantity(itemReq.getQuantity());
            String status = itemReq.getStatus();
            item.setStatus(status == null || status.trim().isEmpty() ? "CREATED" : status);
            orderItemRepository.save(item);
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("order", savedHeader);
        body.put("items", orderItemRepository.findByOrderId(savedHeader.getOrderId()));
        return body;
    }

    // -------- GET ORDER --------
    public Map<String, Object> getOrder(Integer orderId) {
        OrderHeader order = orderHeaderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new NoSuchElementException("Order not found");
        }

        Customer customer = order.getCustomer();
        ContactMech shipping = order.getShippingContact();
        ContactMech billing = order.getBillingContact();
        List<OrderItem> items = order.getItems();

        List<Map<String, Object>> itemDetails = new ArrayList<>();
        if (items != null) {
            for (OrderItem item : items) {
                Product product = item.getProduct();
                Map<String, Object> itemMap = new LinkedHashMap<>();
                itemMap.put("orderId", item.getOrderId());
                itemMap.put("orderItemSeqId", item.getOrderItemSeqId());
                itemMap.put("productId", item.getProductId());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("status", item.getStatus());
                if (product != null) {
                    itemMap.put("productName", product.getProductName());
                    itemMap.put("productColor", product.getColor());
                    itemMap.put("productSize", product.getSize());
                }
                itemDetails.add(itemMap);
            }
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("orderId", order.getOrderId());
        body.put("orderDate", order.getOrderDate());
        body.put("customerId", order.getCustomerId());
        body.put("customer", customer);
        body.put("shippingContact", shipping);
        body.put("billingContact", billing);
        body.put("items", itemDetails);

        return body;
    }

    // -------- UPDATE ORDER --------
    public OrderHeader updateOrder(Integer orderId, OrderHeader request) {
        OrderHeader existing = orderHeaderRepository.findById(orderId).orElse(null);
        if (existing == null) {
            throw new NoSuchElementException("Order not found");
        }

        if (request.getShippingContactMechId() != null) {
            ContactMech shipping = contactMechRepository.findById(request.getShippingContactMechId()).orElse(null);
            if (shipping == null) {
                throw new IllegalArgumentException("Shipping contact not found");
            }
            if (!Objects.equals(shipping.getCustomerId(), existing.getCustomerId())) {
                throw new IllegalArgumentException("Shipping contact does not belong to the order customer");
            }
            existing.setShippingContactMechId(request.getShippingContactMechId());
        }

        if (request.getBillingContactMechId() != null) {
            ContactMech billing = contactMechRepository.findById(request.getBillingContactMechId()).orElse(null);
            if (billing == null) {
                throw new IllegalArgumentException("Billing contact not found");
            }
            if (!Objects.equals(billing.getCustomerId(), existing.getCustomerId())) {
                throw new IllegalArgumentException("Billing contact does not belong to the order customer");
            }
            existing.setBillingContactMechId(request.getBillingContactMechId());
        }

        return orderHeaderRepository.save(existing);
    }

    // -------- DELETE ORDER --------
    public void deleteOrder(Integer orderId) {
        if (!orderHeaderRepository.existsById(orderId)) {
            throw new NoSuchElementException("Order not found");
        }
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        if (!items.isEmpty()) {
            orderItemRepository.deleteAll(items);
        }
        orderHeaderRepository.deleteById(orderId);
    }

    // -------- ADD ITEM --------
    public OrderItem addItem(Integer orderId, OrderItem request) {
        if (!orderHeaderRepository.existsById(orderId)) {
            throw new NoSuchElementException("Order not found");
        }

        if (request.getProductId() == null || request.getQuantity() == null) {
            throw new IllegalArgumentException("Missing productId or quantity");
        }
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        if (productRepository.findById(request.getProductId()).isEmpty()) {
            throw new IllegalArgumentException("Invalid productId: " + request.getProductId());
        }

        List<OrderItem> existingItems = orderItemRepository.findByOrderId(orderId);
        int nextSeq = existingItems.size() + 1;

        String status = request.getStatus();
        if (status == null || status.trim().isEmpty()) {
            status = "CREATED";
        }

        OrderItem item = new OrderItem();
        item.setOrderId(orderId);
        item.setOrderItemSeqId(nextSeq);
        item.setProductId(request.getProductId());
        item.setQuantity(request.getQuantity());
        item.setStatus(status);

        return orderItemRepository.save(item);
    }

    // -------- UPDATE ITEM --------
    public OrderItem updateItem(Integer orderId, Integer orderItemSeqId, OrderItem request) {
        OrderItemId id = new OrderItemId(orderId, orderItemSeqId);
        OrderItem existing = orderItemRepository.findById(id).orElse(null);
        if (existing == null) {
            throw new NoSuchElementException("Order item not found");
        }

        if (request.getQuantity() != null) {
            if (request.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero");
            }
            existing.setQuantity(request.getQuantity());
        }
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            existing.setStatus(request.getStatus());
        }

        return orderItemRepository.save(existing);
    }

    // -------- DELETE ITEM --------
    public void deleteItem(Integer orderId, Integer orderItemSeqId) {
        OrderItemId id = new OrderItemId(orderId, orderItemSeqId);
        if (!orderItemRepository.existsById(id)) {
            throw new NoSuchElementException("Order item not found");
        }
        orderItemRepository.deleteById(id);
    }
}

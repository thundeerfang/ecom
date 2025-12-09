package com.example.hotwax.ecom.controller;

import com.example.hotwax.ecom.entity.ContactMech;
import com.example.hotwax.ecom.entity.Customer;
import com.example.hotwax.ecom.entity.OrderHeader;
import com.example.hotwax.ecom.entity.OrderItem;
import com.example.hotwax.ecom.entity.OrderItemId;
import com.example.hotwax.ecom.entity.Product;
import com.example.hotwax.ecom.repository.ContactMechRepository;
import com.example.hotwax.ecom.repository.CustomerRepository;
import com.example.hotwax.ecom.repository.OrderHeaderRepository;
import com.example.hotwax.ecom.repository.OrderItemRepository;
import com.example.hotwax.ecom.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class OrderApiController {

    @Autowired
    private OrderHeaderRepository orderHeaderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ContactMechRepository contactMechRepository;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody OrderHeader request) {
        if (request.getCustomerId() == null
                || request.getOrderDate() == null
                || request.getShippingContactMechId() == null
                || request.getBillingContactMechId() == null) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }

        Optional<Customer> customerOpt = customerRepository.findById(request.getCustomerId());
        if (customerOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Customer not found");
        }

        Optional<ContactMech> shippingOpt = contactMechRepository.findById(request.getShippingContactMechId());
        Optional<ContactMech> billingOpt = contactMechRepository.findById(request.getBillingContactMechId());
        if (shippingOpt.isEmpty() || billingOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Shipping or billing contact not found");
        }

        ContactMech shippingContact = shippingOpt.get();
        ContactMech billingContact = billingOpt.get();
        if (!Objects.equals(shippingContact.getCustomerId(), request.getCustomerId())
                || !Objects.equals(billingContact.getCustomerId(), request.getCustomerId())) {
            return ResponseEntity.badRequest().body("Contacts do not belong to the given customer");
        }

        List<OrderItem> items = request.getItems();
        if (items == null || items.isEmpty()) {
            return ResponseEntity.badRequest().body("At least one order item is required");
        }

        for (OrderItem itemReq : items) {
            if (itemReq.getProductId() == null || itemReq.getQuantity() == null) {
                return ResponseEntity.badRequest().body("Each item must have productId and quantity");
            }
            if (itemReq.getQuantity() <= 0) {
                return ResponseEntity.badRequest().body("Quantity must be greater than zero");
            }
            Optional<Product> productOpt = productRepository.findById(itemReq.getProductId());
            if (productOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid productId: " + itemReq.getProductId());
            }
        }

        OrderHeader header = new OrderHeader();
        header.setOrderDate(request.getOrderDate());
        header.setCustomerId(request.getCustomerId());
        header.setShippingContactMechId(request.getShippingContactMechId());
        header.setBillingContactMechId(request.getBillingContactMechId());

        OrderHeader savedHeader = orderHeaderRepository.save(header);

        int seq = 1;
        for (OrderItem itemReq : items) {
            String status = itemReq.getStatus();
            if (status == null || status.trim().isEmpty()) {
                status = "CREATED";
            }

            OrderItem item = new OrderItem();
            item.setOrderId(savedHeader.getOrderId());
            item.setOrderItemSeqId(seq);
            item.setProductId(itemReq.getProductId());
            item.setQuantity(itemReq.getQuantity());
            item.setStatus(status);

            orderItemRepository.save(item);
            seq++;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("order", savedHeader);
        body.put("items", orderItemRepository.findByOrderId(savedHeader.getOrderId()));
        return ResponseEntity.status(201).body(body);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Integer orderId) {
        Optional<OrderHeader> orderOpt = orderHeaderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Order not found");
        }

        OrderHeader order = orderOpt.get();

        Customer customer = order.getCustomer();
        ContactMech shipping = order.getShippingContact();
        ContactMech billing = order.getBillingContact();
        List<OrderItem> items = order.getItems();

        List<Map<String, Object>>itemDetails = new ArrayList<>();
        if (items != null) {
            for (OrderItem item : items) {
                Product product = item.getProduct();
                Map<String, Object> itemMap = new HashMap<>();
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

        Map<String, Object> body = new HashMap<>();
        body.put("orderId", order.getOrderId());
        body.put("orderDate", order.getOrderDate());
        body.put("customerId", order.getCustomerId());
        body.put("customer", customer);
        body.put("shippingContact", shipping);
        body.put("billingContact", billing);
        body.put("items", itemDetails);

        return ResponseEntity.ok(body);
    }

    @PutMapping("/orders/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable Integer orderId,
                                         @RequestBody OrderHeader request) {
        Optional<OrderHeader> optional = orderHeaderRepository.findById(orderId);
        if (optional.isEmpty()) {
            return ResponseEntity.status(404).body("Order not found");
        }

        OrderHeader existing = optional.get();

        if (request.getShippingContactMechId() != null) {
            Optional<ContactMech> shippingOpt = contactMechRepository.findById(request.getShippingContactMechId());
            if (shippingOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Shipping contact not found");
            }
            ContactMech shipping = shippingOpt.get();
            if (!Objects.equals(shipping.getCustomerId(), existing.getCustomerId())) {
                return ResponseEntity.badRequest().body("Shipping contact does not belong to the order customer");
            }
            existing.setShippingContactMechId(request.getShippingContactMechId());
        }

        if (request.getBillingContactMechId() != null) {
            Optional<ContactMech> billingOpt = contactMechRepository.findById(request.getBillingContactMechId());
            if (billingOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Billing contact not found");
            }
            ContactMech billing = billingOpt.get();
            if (!Objects.equals(billing.getCustomerId(), existing.getCustomerId())) {
                return ResponseEntity.badRequest().body("Billing contact does not belong to the order customer");
            }
            existing.setBillingContactMechId(request.getBillingContactMechId());
        }

        OrderHeader updated = orderHeaderRepository.save(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Integer orderId) {
        if (!orderHeaderRepository.existsById(orderId)) {
            return ResponseEntity.status(404).body("Order not found");
        }
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        if (!items.isEmpty()) {
            orderItemRepository.deleteAll(items);
        }
        orderHeaderRepository.deleteById(orderId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/orders/{orderId}/items")
    public ResponseEntity<?> addItem(@PathVariable Integer orderId,
                                     @RequestBody OrderItem request) {
        Optional<OrderHeader> orderOpt = orderHeaderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Order not found");
        }

        if (request.getProductId() == null || request.getQuantity() == null) {
            return ResponseEntity.badRequest().body("Missing productId or quantity");
        }
        if (request.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("Quantity must be greater than zero");
        }

        Optional<Product> productOpt = productRepository.findById(request.getProductId());
        if (productOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid productId: " + request.getProductId());
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

        OrderItem saved = orderItemRepository.save(item);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/orders/{orderId}/items/{orderItemSeqId}")
    public ResponseEntity<?> updateItem(@PathVariable Integer orderId,
                                        @PathVariable Integer orderItemSeqId,
                                        @RequestBody OrderItem request) {
        OrderItemId id = new OrderItemId(orderId, orderItemSeqId);
        Optional<OrderItem> optional = orderItemRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.status(404).body("Order item not found");
        }

        OrderItem existing = optional.get();

        if (request.getQuantity() != null) {
            if (request.getQuantity() <= 0) {
                return ResponseEntity.badRequest().body("Quantity must be greater than zero");
            }
            existing.setQuantity(request.getQuantity());
        }
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            existing.setStatus(request.getStatus());
        }

        OrderItem updated = orderItemRepository.save(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/orders/{orderId}/items/{orderItemSeqId}")
    public ResponseEntity<?> deleteItem(@PathVariable Integer orderId,
                                        @PathVariable Integer orderItemSeqId) {
        OrderItemId id = new OrderItemId(orderId, orderItemSeqId);
        if (!orderItemRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Order item not found");
        }
        orderItemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
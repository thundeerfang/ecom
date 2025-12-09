package com.example.hotwax.ecom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@IdClass(OrderItemId.class)
@Table(name = "order_item")
public class OrderItem {

    @Id
    @Column(name = "order_id")
    private Integer orderId;

    @Id
    @Column(name = "order_item_seq_id")
    private Integer orderItemSeqId;

    @Column(name = "product_id")
    private Integer productId;

    private Integer quantity;

    private String status;

    @ManyToOne
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    @JsonIgnore
    private OrderHeader order;

    @ManyToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderItemSeqId() {
        return orderItemSeqId;
    }

    public void setOrderItemSeqId(Integer orderItemSeqId) {
        this.orderItemSeqId = orderItemSeqId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrderHeader getOrder() {
        return order;
    }

    public void setOrder(OrderHeader order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
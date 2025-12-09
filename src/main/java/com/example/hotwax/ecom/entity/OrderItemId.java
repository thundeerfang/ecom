package com.example.hotwax.ecom.entity;

import java.io.Serializable;
import java.util.Objects;

public class OrderItemId implements Serializable {

    private Integer orderId;
    private Integer orderItemSeqId;

    public OrderItemId() {
    }

    public OrderItemId(Integer orderId, Integer orderItemSeqId) {
        this.orderId = orderId;
        this.orderItemSeqId = orderItemSeqId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public Integer getOrderItemSeqId() {
        return orderItemSeqId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId that = (OrderItemId) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(orderItemSeqId, that.orderItemSeqId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, orderItemSeqId);
    }
}
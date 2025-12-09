package com.example.hotwax.ecom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "order_header")
public class OrderHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "shipping_contact_mech_id")
    private Integer shippingContactMechId;

    @Column(name = "billing_contact_mech_id")
    private Integer billingContactMechId;

    @ManyToOne
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    @JsonIgnore
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "shipping_contact_mech_id", insertable = false, updatable = false)
    @JsonIgnore
    private ContactMech shippingContact;

    @ManyToOne
    @JoinColumn(name = "billing_contact_mech_id", insertable = false, updatable = false)
    @JsonIgnore
    private ContactMech billingContact;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getShippingContactMechId() {
        return shippingContactMechId;
    }

    public void setShippingContactMechId(Integer shippingContactMechId) {
        this.shippingContactMechId = shippingContactMechId;
    }

    public Integer getBillingContactMechId() {
        return billingContactMechId;
    }

    public void setBillingContactMechId(Integer billingContactMechId) {
        this.billingContactMechId = billingContactMechId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ContactMech getShippingContact() {
        return shippingContact;
    }

    public void setShippingContact(ContactMech shippingContact) {
        this.shippingContact = shippingContact;
    }

    public ContactMech getBillingContact() {
        return billingContact;
    }

    public void setBillingContact(ContactMech billingContact) {
        this.billingContact = billingContact;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
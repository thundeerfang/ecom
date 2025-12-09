package com.example.hotwax.ecom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<ContactMech> contacts;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<OrderHeader> orders;

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<ContactMech> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactMech> contacts) {
        this.contacts = contacts;
    }

    public List<OrderHeader> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderHeader> orders) {
        this.orders = orders;
    }
}
package com.example.hotwax.ecom.controller;

import com.example.hotwax.ecom.entity.ContactMech;
import com.example.hotwax.ecom.entity.Customer;
import com.example.hotwax.ecom.repository.ContactMechRepository;
import com.example.hotwax.ecom.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomerApiController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ContactMechRepository contactMechRepository;

    @GetMapping("/customers")
    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @GetMapping("/customers/{customerId}/contacts")
    public ResponseEntity<List<ContactMech >> getContacts(@PathVariable Integer customerId) {
        List<ContactMech> contacts = contactMechRepository.findByCustomerId(customerId);
        if (contacts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(contacts);
    }
}
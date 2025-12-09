package com.example.hotwax.ecom.repository;

import com.example.hotwax.ecom.entity.ContactMech;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactMechRepository extends JpaRepository<ContactMech, Integer> {

    List<ContactMech> findByCustomerId(Integer customerId);
}

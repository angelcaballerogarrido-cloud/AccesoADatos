package com.example.accessingdatajpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    // Spring Data JPA generará automáticamente la consulta SQL para este método!
    List<Customer> findByLastName(String lastName);

    Customer findById(long id);
}

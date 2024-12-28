package com.Application.CBS.repository;

import com.Application.CBS.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, BigInteger> {
    Optional<Customer> findByCustomerMobileAndCustomerEmail(String mobile, String email);
}

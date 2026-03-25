package com.basic.app.repository;

import com.basic.app.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Handles IllegalArgumentException and returns an appropriate HTTP 400 response.
 *
 * @param ex The IllegalArgumentException that was thrown
 * @return A ResponseEntity with HTTP status 400 and the exception message
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
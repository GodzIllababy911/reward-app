package com.basic.app.repository;

import com.basic.app.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Transaction entities in the database.
 * Provides standard CRUD operations and custom query methods for transaction data.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Retrieves all transactions associated with a specific customer.
     *
     * @param customerId The unique identifier of the customer
     * @return A list of Transaction objects belonging to the specified customer
     */
    List<Transaction> findByCustomerId(Long customerId);
}


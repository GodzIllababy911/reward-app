package com.basic.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entity representing a financial transaction in the rewards program.
 * Each transaction has an associated customer, monetary amount, and date.
 */
@Entity
@Getter
@Setter
@Table(name = "transactions")
public class Transaction {

    /**
     * Unique identifier for the transaction.
     * This primary key in the database.
     */
    @Id
    private Long id;

    /**
     * The customer associated with this transaction.
     * This establishes a many-to-one relationship where multiple transactions
     * can belong to a single customer.
     */
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    /**
     * The monetary amount of the transaction.
     * This represents the purchase amount in dollars.
     */
    private double amount;

    /**
     * The date when the transaction occurred.
     * Stored as a LocalDate to represent the calendar date without time components.
     */
    @Column(name = "transaction_date")
    private LocalDate transactionDate;

}


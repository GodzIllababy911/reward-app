package com.basic.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a customer in the rewards program.
 * Contains basic customer identification information.
 */
@Entity
@Getter
@Setter
@Table(name = "customers")
public class Customer {

    /**
     * Unique identifier for the customer.
     * This serves as the primary key in the database.
     */
    @Id
    private Long id;

     /**
     * The name of the customer.
     * Represents the full name or display name of the customer.
     */
    private String name;

}

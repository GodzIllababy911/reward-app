package com.basic.app.controller;

import com.basic.app.dto.RewardResponse;
import com.basic.app.service.RewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing reward calculations and retrieval.
 * Provides endpoints for accessing customer reward point information.
 */
@RestController
@RequestMapping("/rewards")
public class RewardController {

     private static final Logger log = LoggerFactory.getLogger(RewardController.class);
    

    /**
     * The service used to calculate reward points for customers.
     */
    private final RewardService service;

    /**
     * Constructs a new RewardController with the specified reward service.
     *
     * @param service The service used to calculate reward points
     */
    public RewardController(RewardService service) {
        this.service = service;
    }

    /**
     * Retrieves reward points for a specific customer.
     * Calculates monthly breakdown and total points for all customer transactions.
     *
     * @param customerId The unique identifier of the customer
     * @return A RewardResponse object containing monthly and total reward points
     */
    @GetMapping("/{customerId}")
    public RewardResponse getRewards(@PathVariable Long customerId) {
        log.info("Received request to get rewards for customer ID: {}", customerId);
        
        return service.calculateRewards(customerId);
    }
}

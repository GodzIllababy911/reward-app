package com.basic.app.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object representing the reward points calculation results for a customer.
 * Contains monthly breakdown of earned points and total accumulated points.
 */
@Getter
@Setter
public class RewardResponse {

    /**
     * The unique identifier of the customer for whom rewards are calculated.
     */
    private Long customerId;
    
    /**
     * A map containing monthly reward points breakdown.
     * Key: Month identifier (e.g., "January 2023")
     * Value: Total reward points earned in that month
     */
    private Map<String, Integer> monthlyRewards;
    
    /**
     * The total number of reward points accumulated across all months.
     */
    private int totalRewards;

    /**
     * Constructs a new RewardResponse instance.
     *
     * @param customerId The unique identifier of the customer
     * @param monthlyRewards A map of monthly reward points breakdown
     * @param totalRewards The total accumulated reward points
     */
    public RewardResponse(Long customerId, Map<String, Integer> monthlyRewards, int totalRewards) {
        this.customerId = customerId;
        this.monthlyRewards = monthlyRewards;
        this.totalRewards = totalRewards;
    }

}


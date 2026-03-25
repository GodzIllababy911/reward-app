package com.basic.app.service;

import com.basic.app.dto.RewardResponse;
import com.basic.app.exception.ResourceNotFoundException;
import com.basic.app.model.Transaction;
import com.basic.app.repository.CustomerRepository;
import com.basic.app.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/**
 * Service class responsible for calculating reward points based on customer transactions.
 * Implements the business logic for the rewards program according to the specified rules.
 * Validates customer existence and ensures transactions meet business requirements.
 */
@Service
public class RewardService {
    private static final Logger log = LoggerFactory.getLogger(RewardService.class); 
    private static final int LOWER_LIMIT = 50;
    private static final int UPPER_LIMIT = 100;

    /**
     * The repository used to retrieve transaction data.
     */
    private final TransactionRepository transactionRepository;

    /**
     * The repository used to verify customer existence.
     */
    private final CustomerRepository customerRepository;

    /**
     * Constructs a new RewardService with the specified repositories.
     *
     * @param transactionRepository The repository used to retrieve transaction data
     * @param customerRepository The repository used to verify customer existence
     */
    public RewardService(TransactionRepository transactionRepository,
                        CustomerRepository customerRepository) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        log.info("RewardService initialized successfully");
    }

    /**
     * Calculates reward points for a specific customer across their transactions
     * within the last three months from the most recent transaction date.
     * Groups transactions by month and calculates both monthly and total points.
     *
     * @param customerId The unique identifier of the customer
     * @return A RewardResponse object containing monthly breakdown and total points
     * @throws IllegalArgumentException if the customerId is invalid (null or <= 0)
     * @throws ResourceNotFoundException if the customer doesn't exist or has no transactions
     */
    public RewardResponse calculateRewards(Long customerId) {
    log.info("Calculating rewards for customer ID: {}", customerId);

    if (customerId == null || customerId <= 0) {
        log.error("Invalid customerId provided: {}", customerId);
        throw new IllegalArgumentException("Invalid customerId");
    }

    boolean customerExists = customerRepository.existsById(customerId);


    if (!customerExists) {
        log.warn("Customer not found with id: {}", customerId);
        throw new ResourceNotFoundException("Customer not found with id: " + customerId);
    }
    
    List<Transaction> transactions = transactionRepository.findByCustomerId(customerId);

    if (transactions.isEmpty()) {
       log.warn("No transactions found for customer: {}", customerId);
       throw new ResourceNotFoundException("No transactions found for customer: " + customerId);
    }

    log.debug("Found {} transactions for customer ID: {}", transactions.size(), customerId);

    List<Transaction> validTransactions = transactions.stream()
            .filter(t -> t.getTransactionDate() != null)
            .toList();

    if (validTransactions.isEmpty()) {
        log.info("No valid transactions with dates found for customer: {}. Returning empty response.", customerId);

        return new RewardResponse(customerId, new TreeMap<>(), 0);
    }

    LocalDate maxDate = validTransactions.stream()
            .map(Transaction::getTransactionDate)
            .max(LocalDate::compareTo)
            .orElseThrow(() -> {
                    log.error("Unable to determine maximum transaction date for customer: {}", customerId);
                    return new IllegalStateException("Unable to determine maximum transaction date");
                });

    log.debug("Max transaction date for customer {}: {}", customerId, maxDate);


    LocalDate threeMonthsAgo = maxDate.minusMonths(2).withDayOfMonth(1);
    log.debug("Filtering transactions from date: {} for customer: {}", threeMonthsAgo, customerId);

    List<Transaction> filteredTransactions = validTransactions.stream()
            .filter(t -> t.getAmount() >= 0)
            .filter(t -> t.getTransactionDate() != null)
            .filter(t -> !t.getTransactionDate().isBefore(threeMonthsAgo))
            .toList();

    if (filteredTransactions.isEmpty()) {
        log.warn("No transactions found in last 3 months for customer: {}", customerId);
        throw new ResourceNotFoundException("No transactions found in last 3 months for customer: " + customerId);
    }

    log.info("Processing {} transactions for reward calculation for customer: {}", filteredTransactions.size(), customerId);


    Map<String, Integer> monthlyPoints = new HashMap<>();

    for (Transaction t : filteredTransactions) {
        int points = calculatePoints(t.getAmount());

        String month = YearMonth.from(t.getTransactionDate()).toString();

        monthlyPoints.put(month,
                monthlyPoints.getOrDefault(month, 0) + points);
    
                    log.debug("Added {} points for transaction on {} (total for month: {})", points, t.getTransactionDate());

            }

    

    int total = monthlyPoints.values()
            .stream()
            .mapToInt(Integer::intValue)
            .sum();

    log.info("Calculated total reward points for customer {}: {}", customerId, total);
    log.debug("Monthly breakdown for customer {}: {}", customerId, monthlyPoints);


    return new RewardResponse(customerId, monthlyPoints, total);
}

    /**
     * Calculates reward points for a single transaction based on the following rules:
     * - 0 points for amounts $50 or less
     * - 1 point per dollar for amounts between $50 and $100
     * - 2 points per dollar for amounts over $100 (plus 1 point per dollar for the first $50-$100 range)
     *
     * @param amount The transaction amount in dollars
     * @return The calculated reward points for the transaction, rounded down to nearest integer
     */
    private int calculatePoints(double amount) {
        log.trace("Calculating points for transaction amount: {}", amount);

        if (amount <= LOWER_LIMIT){
            log.trace("Amount {} is <= {}, returning 0 points", amount, LOWER_LIMIT);
            return 0;
        }

        if (amount <= UPPER_LIMIT) {
             
            log.trace("Amount {} is between {} and {}, returning {} points", amount, LOWER_LIMIT, UPPER_LIMIT,(int) Math.floor(amount - LOWER_LIMIT));
            return (int) Math.floor(amount - LOWER_LIMIT);
    }

        log.trace("Amount {} is > {}, returning {} points", amount, UPPER_LIMIT, (int) Math.floor((amount - UPPER_LIMIT) * 2 +(UPPER_LIMIT - LOWER_LIMIT)));
        return (int) Math.floor((amount - UPPER_LIMIT) * 2 +(UPPER_LIMIT - LOWER_LIMIT));
       
    }
}

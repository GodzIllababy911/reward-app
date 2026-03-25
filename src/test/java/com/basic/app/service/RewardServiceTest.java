package com.basic.app.service;

import com.basic.app.dto.RewardResponse;
import com.basic.app.exception.ResourceNotFoundException;
import com.basic.app.model.Customer;
import com.basic.app.model.Transaction;
import com.basic.app.repository.CustomerRepository;
import com.basic.app.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RewardServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private RewardService rewardService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCalculateRewardsSuccessfully() {
        when(customerRepository.existsById(1L)).thenReturn(true);

        List<Transaction> transactions = List.of(
                txn(1L, 120, "2026-03-10"),
                txn(1L, 80, "2026-04-10"),
                txn(1L, 200, "2026-05-10")
        );

        when(transactionRepository.findByCustomerId(1L)).thenReturn(transactions);

        RewardResponse response = rewardService.calculateRewards(1L);

        assertNotNull(response);
        assertEquals(1L, response.getCustomerId());
        assertEquals(3, response.getMonthlyRewards().size());
        assertTrue(response.getTotalRewards() > 0);
    }

    @Test
    void shouldThrowForInvalidCustomerId() {
        assertThrows(IllegalArgumentException.class,
                () -> rewardService.calculateRewards(-1L));
    }

    @Test
    void shouldThrowWhenCustomerNotExists() {
        when(customerRepository.existsById(2L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> rewardService.calculateRewards(2L));
    }

    @Test
    void shouldThrowWhenNoTransactions() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.findByCustomerId(1L)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> rewardService.calculateRewards(1L));
    }

    @Test
    void shouldReturnEmptyWhenAllDatesNull() {
        when(customerRepository.existsById(1L)).thenReturn(true);

        List<Transaction> transactions = List.of(
                txnWithNullDate(1L, 100)
        );

        when(transactionRepository.findByCustomerId(1L)).thenReturn(transactions);

        RewardResponse response = rewardService.calculateRewards(1L);

        assertEquals(0, response.getTotalRewards());
        assertTrue(response.getMonthlyRewards().isEmpty());
    }

   @Test
    void shouldThrowWhenNoTransactionsInLast3Months() {

        when(customerRepository.existsById(1L)).thenReturn(true);

        List<Transaction> transactions = List.of(
                txn(1L, -100, "2026-05-01") // negative → filtered out
        );

        when(transactionRepository.findByCustomerId(1L)).thenReturn(transactions);

        assertThrows(ResourceNotFoundException.class,
                () -> rewardService.calculateRewards(1L));
    }

    @Test
    void shouldIgnoreNegativeTransactions() {
        when(customerRepository.existsById(1L)).thenReturn(true);

        List<Transaction> transactions = List.of(
                txn(1L, -100, "2026-05-01"),
                txn(1L, 120, "2026-05-02")
        );

        when(transactionRepository.findByCustomerId(1L)).thenReturn(transactions);

        RewardResponse response = rewardService.calculateRewards(1L);

        assertTrue(response.getTotalRewards() > 0);
    }

    @Test
    void shouldCoverAllCalculatePointsBranches() throws Exception {

        Method method = RewardService.class
                .getDeclaredMethod("calculatePoints", double.class);

        method.setAccessible(true);

        assertEquals(0, method.invoke(rewardService, 50));
        assertEquals(0, method.invoke(rewardService, 49));
        assertEquals(50, method.invoke(rewardService, 100));
        assertEquals(90, method.invoke(rewardService, 120));
    }

    private Transaction txn(Long customerId, double amount, String date) {
        Transaction t = new Transaction();

        Customer customer = new Customer();
        customer.setId(customerId);

        t.setCustomer(customer);
        t.setAmount(amount);
        t.setTransactionDate(LocalDate.parse(date));

        return t;
    }

    private Transaction txnWithNullDate(Long customerId, double amount) {
        Transaction t = new Transaction();

        Customer customer = new Customer();
        customer.setId(customerId);

        t.setCustomer(customer);
        t.setAmount(amount);
        t.setTransactionDate(null);

        return t;
    }
}
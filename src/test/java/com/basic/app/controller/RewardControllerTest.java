package com.basic.app.controller;

import com.basic.app.dto.RewardResponse;
import com.basic.app.exception.ResourceNotFoundException;
import com.basic.app.service.RewardService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import java.util.TreeMap;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RewardControllerTest {

    private MockMvc mockMvc;

    private RewardService rewardService;

    @BeforeEach
    void setup() {
        rewardService = Mockito.mock(RewardService.class);

        RewardController controller = new RewardController(rewardService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new com.basic.app.exception.GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturn200ForValidCustomer() throws Exception {

        RewardResponse response = new RewardResponse(
                1L,
                new TreeMap<>(Map.of("2026-05", 90)),
                90
        );

        when(rewardService.calculateRewards(anyLong())).thenReturn(response);

        mockMvc.perform(get("/rewards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.totalRewards").value(90))
                .andExpect(jsonPath("$.monthlyRewards['2026-05']").value(90));
    }

    @Test
    void shouldReturn200WithMultipleMonths() throws Exception {

        RewardResponse response = new RewardResponse(
                1L,
                new TreeMap<>(Map.of(
                    "2026-05", 90,
                    "2026-06", 120,
                    "2026-07", 150
                )),
                360
        );

        when(rewardService.calculateRewards(anyLong())).thenReturn(response);

        mockMvc.perform(get("/rewards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.totalRewards").value(360))
                .andExpect(jsonPath("$.monthlyRewards['2026-05']").value(90))
                .andExpect(jsonPath("$.monthlyRewards['2026-06']").value(120))
                .andExpect(jsonPath("$.monthlyRewards['2026-07']").value(150));
    }

    @Test
    void shouldReturn404WhenCustomerNotFound() throws Exception {

        when(rewardService.calculateRewards(anyLong()))
                .thenThrow(new ResourceNotFoundException("Customer not found with id: 2"));

        mockMvc.perform(get("/rewards/2"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer not found with id: 2"));
    }

    @Test
    void shouldReturn404WhenNoTransactions() throws Exception {

        when(rewardService.calculateRewards(anyLong()))
                .thenThrow(new ResourceNotFoundException("No transactions found for customer: 3"));

        mockMvc.perform(get("/rewards/3"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No transactions found for customer: 3"));
    }

    @Test
    void shouldReturn404WhenNoTransactionsInLastThreeMonths() throws Exception {

        when(rewardService.calculateRewards(anyLong()))
                .thenThrow(new ResourceNotFoundException("No transactions found in last 3 months for customer: 4"));

        mockMvc.perform(get("/rewards/4"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No transactions found in last 3 months for customer: 4"));
    }

    @Test
    void shouldReturn400ForInvalidInputString() throws Exception {

        mockMvc.perform(get("/rewards/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid customerId. It must be a number."));
    }

    @Test
    void shouldReturn400ForInvalidInputSpecialCharacters() throws Exception {

        mockMvc.perform(get("/rewards/@#$"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid customerId. It must be a number."));
    }

    @Test
    void shouldReturn400ForNegativeId() throws Exception {

        when(rewardService.calculateRewards(anyLong()))
                .thenThrow(new IllegalArgumentException("Invalid customerId"));

        mockMvc.perform(get("/rewards/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid customerId"));
    }

    @Test
    void shouldReturn400ForZeroId() throws Exception {

        when(rewardService.calculateRewards(anyLong()))
                .thenThrow(new IllegalArgumentException("Invalid customerId"));

        mockMvc.perform(get("/rewards/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid customerId"));
    }

    @Test
    void shouldReturn400ForLargeNumberId() throws Exception {

        long largeId = 999999999999999999L;
        
        RewardResponse response = new RewardResponse(
                largeId,
                new TreeMap<>(Map.of("2026-05", 90)),
                90
        );

        when(rewardService.calculateRewards(anyLong())).thenReturn(response);

        mockMvc.perform(get("/rewards/" + largeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(largeId));
    }

    @Test
    void shouldHandleEmptyMonthlyRewards() throws Exception {

        RewardResponse response = new RewardResponse(
                5L,
                new TreeMap<>(),
                0
        );

        when(rewardService.calculateRewards(anyLong())).thenReturn(response);

        mockMvc.perform(get("/rewards/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(5))
                .andExpect(jsonPath("$.totalRewards").value(0))
                .andExpect(jsonPath("$.monthlyRewards").isEmpty());
    }
}
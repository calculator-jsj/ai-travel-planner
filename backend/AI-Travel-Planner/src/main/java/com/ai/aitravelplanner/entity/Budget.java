package com.ai.aitravelplanner.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class Budget {
    private Long id;
    private Long planId;
    private String category;
    private BigDecimal amount;
    private String remark;
    private LocalDate expenseDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
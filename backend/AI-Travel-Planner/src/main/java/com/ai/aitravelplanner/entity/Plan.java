package com.ai.aitravelplanner.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Data
public class Plan {
    private Long id;
    private Long userId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer days;
    private BigDecimal budget;
    private String preferences; // 存 JSON 字符串 ["美食","动漫"]
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 非表字段
    private List<Spot> spots;
    private List<Budget> budgets;
}

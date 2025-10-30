package com.ai.aitravelplanner.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class Spot {
    private Long id;
    private Long planId;
    private Integer day;
    private String name;
    private Double lng;
    private Double lat;
    private String type;
    private String description;
    private Integer orderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
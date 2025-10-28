package com.ai.aitravelplanner.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserPreference {
    private Long id;
    private Long userId;
    private String preference;
    private String travelStyle;
    private LocalDateTime createdAt;
}
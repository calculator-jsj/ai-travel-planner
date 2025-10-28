package com.ai.aitravelplanner.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

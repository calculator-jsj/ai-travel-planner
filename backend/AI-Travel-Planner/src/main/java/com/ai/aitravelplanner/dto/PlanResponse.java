package com.ai.aitravelplanner.dto;

import lombok.Data;
import java.util.List;

@Data
public class PlanResponse {
    private List<PlanDayDTO> plan;     // 整体行程安排
}
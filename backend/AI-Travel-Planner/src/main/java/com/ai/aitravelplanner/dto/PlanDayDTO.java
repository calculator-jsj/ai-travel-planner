package com.ai.aitravelplanner.dto;

import lombok.Data;
import java.util.List;

/**
 * AI 生成的单天行程
 */
@Data
public class PlanDayDTO {
    private Integer day;               // 第几天
    private List<SpotDTO> spots;       // 当天的景点详细信息
}
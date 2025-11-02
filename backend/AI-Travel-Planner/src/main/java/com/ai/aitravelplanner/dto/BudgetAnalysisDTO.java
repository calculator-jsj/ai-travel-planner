package com.ai.aitravelplanner.dto;

import lombok.Data;
import java.util.List;

/**
 * 预算分析结果DTO
 */
@Data
public class BudgetAnalysisDTO {
    /**
     * 消费趋势分析
     */
    private String consumptionTrend;
    
    /**
     * 优化建议列表
     */
    private List<String> suggestions;
    
    /**
     * 预算使用情况总结
     */
    private String budgetSummary;
    
    /**
     * 风险提示
     */
    private String riskWarning;
}


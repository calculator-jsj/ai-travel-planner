package com.ai.aitravelplanner.controller;

import com.ai.aitravelplanner.dto.BudgetAnalysisDTO;
import com.ai.aitravelplanner.result.Result;
import com.ai.aitravelplanner.service.BudgetAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 预算AI分析控制器
 */
@RestController
@RequestMapping("/api/ai/budget")
public class BudgetAiController {
    
    @Autowired
    private BudgetAiService budgetAiService;

    @PostMapping("/analyze")
    public Result<BudgetAnalysisDTO> analyzeBudget(@RequestBody Map<String, Object> payload) {
        BudgetAnalysisDTO analysis = budgetAiService.analyzeBudget(payload);
        return Result.success(analysis);
    }
}


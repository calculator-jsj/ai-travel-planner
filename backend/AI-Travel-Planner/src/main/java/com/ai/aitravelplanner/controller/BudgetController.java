package com.ai.aitravelplanner.controller;

import com.ai.aitravelplanner.entity.Budget;
import com.ai.aitravelplanner.result.Result;
import com.ai.aitravelplanner.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping("/add")
    public Result addBudget(@RequestBody Budget budget) {
        boolean ok = budgetService.addBudget(budget);
        if (ok) {
            return Result.success("新增成功");
        } else {
            return Result.error("新增失败");
        }
    }

    @GetMapping("/list/{planId}")
    public Result<List<Budget>> getBudgetByPlanId(@PathVariable Long planId) {
        List<Budget> budgets = budgetService.getBudgetByPlanId(planId);
        return Result.success(budgets);
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteBudget(@PathVariable Long id) {
        boolean success = budgetService.deleteBudget(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error("删除失败，请重试！");
        }
    }
}

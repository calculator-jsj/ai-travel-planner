package com.ai.aitravelplanner.service.impl;

import com.ai.aitravelplanner.entity.Budget;
import com.ai.aitravelplanner.mapper.BudgetMapper;
import com.ai.aitravelplanner.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {
    @Autowired
    private BudgetMapper budgetMapper;

    @Override
    public boolean addBudget(Budget budget) {
        budget.setCreatedAt(LocalDateTime.now());
        budget.setUpdatedAt(LocalDateTime.now());

        int rows = budgetMapper.insert(budget);
        return rows > 0;
    }

    @Override
    public List<Budget> getBudgetByPlanId(Long planId) {
        return budgetMapper.getByPlanId(planId);
    }

    @Override
    public boolean deleteBudget(Long id) {
        return budgetMapper.deleteById(id) > 0;
    }
}

package com.ai.aitravelplanner.service;

import com.ai.aitravelplanner.entity.Budget;

import java.util.List;

public interface BudgetService {

    /**
     * 添加预算
     * @param budget
     * @return
     */
    boolean addBudget(Budget budget);

    /**
     * 根据计划id查询预算
     * @param planId
     * @return
     */
    List<Budget> getBudgetByPlanId(Long planId);

    /**
     * 删除预算
     * @param id
     * @return
     */
    boolean deleteBudget(Long id);
}

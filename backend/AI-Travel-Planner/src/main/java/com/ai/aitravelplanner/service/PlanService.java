package com.ai.aitravelplanner.service;

import com.ai.aitravelplanner.dto.PlanDayDTO;
import com.ai.aitravelplanner.dto.PlanRequestDTO;
import com.ai.aitravelplanner.dto.PlanResponse;
import com.ai.aitravelplanner.entity.Plan;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface PlanService {

    /**
     * 生成旅行计划
     * @param payload 请求参数
     * @return 旅行计划
     */
    PlanResponse generatePlan(Map<String, Object> payload);

    /**
     * 保存行程和景点xx
     * @param userId 用户ID
     * @param request 请求参数
     */
    void savePlan(Long userId, PlanRequestDTO request);

    /**
     * 根据用户ID获取所有行程
     * @param userId 用户ID
     * @return 所有行程
     */
    List<Plan> getPlansByUserId(Long userId);

    /**
     * 搜索行程
     * @param userId 用户ID
     * @param planName 行程名称
     * @param preferences 用户偏好
     * @param budgetMin 最小预算
     * @param budgetMax 最大预算
     * @return 搜索结果
     */
    List<Plan> searchPlans(Long userId, String planName, String preferences, Double budgetMin, Double budgetMax);

    /**
     * 删除行程和景点
     * @param planId 行程ID
     */
    void deletePlanWithSpots(Long planId);
}

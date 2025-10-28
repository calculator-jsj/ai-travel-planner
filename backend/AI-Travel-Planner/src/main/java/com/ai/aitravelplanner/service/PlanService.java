package com.ai.aitravelplanner.service;

import com.ai.aitravelplanner.dto.PlanDayDTO;
import com.ai.aitravelplanner.dto.PlanResponse;
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
}

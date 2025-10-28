package com.ai.aitravelplanner.service.impl;

import com.ai.aitravelplanner.dto.PlanDayDTO;
import com.ai.aitravelplanner.dto.PlanResponse;
import com.ai.aitravelplanner.service.PlanService;
import com.ai.aitravelplanner.service.TravelPlanAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PlanServiceImpl implements PlanService {

    @Autowired
    private TravelPlanAiService travelPlanAiService;

    @Override
    public PlanResponse generatePlan(Map<String, Object> payload) {
        return travelPlanAiService.generatePlan(payload);
    }
}

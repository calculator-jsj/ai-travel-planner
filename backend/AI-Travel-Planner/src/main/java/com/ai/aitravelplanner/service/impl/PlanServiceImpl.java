package com.ai.aitravelplanner.service.impl;

import com.ai.aitravelplanner.dto.PlanRequestDTO;
import com.ai.aitravelplanner.dto.PlanResponse;
import com.ai.aitravelplanner.dto.SpotDTO;
import com.ai.aitravelplanner.entity.Plan;
import com.ai.aitravelplanner.entity.Spot;
import com.ai.aitravelplanner.mapper.PlanMapper;
import com.ai.aitravelplanner.mapper.SpotMapper;
import com.ai.aitravelplanner.service.PlanService;
import com.ai.aitravelplanner.service.TravelPlanAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PlanServiceImpl implements PlanService {

    @Autowired
    private TravelPlanAiService travelPlanAiService;

    @Autowired
    private PlanMapper planMapper;
    @Autowired
    private SpotMapper spotMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PlanResponse generatePlan(Map<String, Object> payload) {
        return travelPlanAiService.generatePlan(payload);
    }

    @Override
    @Transactional
    public void savePlan(Long userId, PlanRequestDTO dto) {
        // 保存 plan 主表
        Plan plan = new Plan();
        plan.setUserId(userId);
        plan.setTitle(dto.getPlanName());
        plan.setDays(dto.getDays());
        plan.setBudget(dto.getBudget());
        plan.setPreferences(objectMapper.valueToTree(dto.getPreferences()).toString());
        plan.setCreatedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());

        planMapper.insert(plan); // 自动生成 plan.id

        // 保存每一天的景点
        if (dto.getPlan() != null) {
            dto.getPlan().forEach(dayPlan -> {
                int order = 1;
                for (SpotDTO spotDTO : dayPlan.getSpots()) {
                    Spot spot = new Spot();
                    spot.setPlanId(plan.getId());
                    spot.setDay(dayPlan.getDay());
                    spot.setName(spotDTO.getName());
                    spot.setLng(spotDTO.getLng());
                    spot.setLat(spotDTO.getLat());
                    spot.setType(spotDTO.getType());
                    spot.setDescription(spotDTO.getDescription());
                    spot.setOrderIndex(order++);
                    spot.setCreatedAt(LocalDateTime.now());
                    spot.setUpdatedAt(LocalDateTime.now());
                    spotMapper.insert(spot);
                }
            });
        }
    }

    @Override
    public List<Plan> getPlansByUserId(Long userId) {
        return planMapper.selectByUserId(userId);
    }

    @Override
    public List<Plan> searchPlans(Long userId, String planName, String preferences, Double budgetMin, Double budgetMax) {
        return planMapper.searchPlans(userId, planName, preferences, budgetMin, budgetMax);
    }

    @Override
    public void deletePlanWithSpots(Long planId) {
        // 1. 删除关联的景点
        spotMapper.deleteByPlanId(planId);

        // 2. 删除行程
        planMapper.deleteById(planId);
    }
}

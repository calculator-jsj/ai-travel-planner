package com.ai.aitravelplanner.service;

import com.ai.aitravelplanner.entity.Spot;

import java.util.List;

public interface SpotService {
    /**
     * 根据计划ID获取 spots
     * @param planId 计划ID
     * @return spots
     */
    List<Spot> getSpotsByPlanId(Long planId);
}

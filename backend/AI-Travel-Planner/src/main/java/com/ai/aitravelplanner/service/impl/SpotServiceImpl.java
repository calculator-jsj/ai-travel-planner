package com.ai.aitravelplanner.service.impl;

import com.ai.aitravelplanner.entity.Spot;
import com.ai.aitravelplanner.mapper.SpotMapper;
import com.ai.aitravelplanner.service.SpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpotServiceImpl implements SpotService {

    @Autowired
    private SpotMapper spotMapper;
    @Override
    public List<Spot> getSpotsByPlanId(Long planId) {
        return spotMapper.findByPlanId(planId);
    }
}

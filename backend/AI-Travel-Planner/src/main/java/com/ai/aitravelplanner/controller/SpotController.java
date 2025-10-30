package com.ai.aitravelplanner.controller;

import com.ai.aitravelplanner.entity.Spot;
import com.ai.aitravelplanner.result.Result;
import com.ai.aitravelplanner.service.SpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spot")
@CrossOrigin
public class SpotController {

    @Autowired
    private SpotService spotService;

    @GetMapping("/list/{planId}")
    public Result<List<Spot>> listByPlanId(@PathVariable Long planId) {
        List<Spot> spots = spotService.getSpotsByPlanId(planId);
        return Result.success(spots);
    }
}
package com.ai.aitravelplanner.controller;

import com.ai.aitravelplanner.dto.PlanDayDTO;
import com.ai.aitravelplanner.dto.PlanResponse;
import com.ai.aitravelplanner.dto.SpotDTO;
import com.ai.aitravelplanner.result.Result;
import com.ai.aitravelplanner.service.PlanService;
import com.ai.aitravelplanner.service.TravelPlanAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIPlanController {
    @Autowired
    private PlanService planService;

    @PostMapping("/generate")
    public Result<PlanResponse> generatePlan(@RequestBody Map<String, Object> payload) {
        PlanResponse response = planService.generatePlan(payload);
        return Result.success(response);
    }
}

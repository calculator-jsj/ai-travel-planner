package com.ai.aitravelplanner.controller;

import com.ai.aitravelplanner.dto.PlanRequestDTO;
import com.ai.aitravelplanner.entity.Plan;
import com.ai.aitravelplanner.result.Result;
import com.ai.aitravelplanner.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
public class PlanController {

    @Autowired
    private PlanService planService;

    /**
     * 保存 AI 生成的行程
     */
    @PostMapping("/save")
    public Result savePlan(@RequestBody PlanRequestDTO request,
                           @RequestParam Long userId) {
        planService.savePlan(userId, request);
        return Result.success();
    }

    // 获取用户所有行程
    @GetMapping("/list/{userId}")
    public Result<List<Plan>> getPlansByUserId(@PathVariable Long userId) {
        List<Plan> plans = planService.getPlansByUserId(userId);
        return Result.success(plans);
    }

    /**
     * 搜索用户行程
     */
    @GetMapping("/search")
    public Result<List<Plan>> searchPlans(
            @RequestParam Long userId,
            @RequestParam(required = false) String planName,
            @RequestParam(required = false) String preferences,
            @RequestParam(required = false) Double budgetMin,
            @RequestParam(required = false) Double budgetMax) {

        List<Plan> list = planService.searchPlans(userId, planName, preferences, budgetMin, budgetMax);
        return Result.success(list);
    }

    /**
     * 删除指定计划及关联的景点
     */
    @DeleteMapping("/delete/{planId}")
    public Result<String> deletePlan(@PathVariable Long planId) {
        try {
            planService.deletePlanWithSpots(planId);
            return Result.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}

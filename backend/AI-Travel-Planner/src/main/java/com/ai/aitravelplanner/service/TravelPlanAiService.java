package com.ai.aitravelplanner.service;

import com.ai.aitravelplanner.dto.PlanDayDTO;
import com.ai.aitravelplanner.dto.PlanResponse;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

import java.util.List;
import java.util.Map;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "openAiChatModel"
)
public interface TravelPlanAiService {

    @UserMessage("""
        你是一位专业的旅行规划AI助手。
        用户将提供目的地、天数、预算、人数以及旅行偏好。
        请根据这些信息生成详细的每日旅行计划，返回格式为 JSON。
        务必做到经纬度信息准确！
        
        输出格式必须严格遵循以下结构：
        {
          "plan": [
            {
              "day": 1,
              "spots": [
                {"name": "景点A", "lng": 116.397, "lat": 39.908, "description": "描述A"},
                {"name": "景点B", "lng": 116.384, "lat": 39.925, "description": "描述B"}
              ]
            },
            ...
          ]
        }

        用户请求参数：{{payload}}
    """)
    PlanResponse generatePlan(Map<String, Object> payload);
}

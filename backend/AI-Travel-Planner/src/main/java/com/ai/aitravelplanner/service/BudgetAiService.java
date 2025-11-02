package com.ai.aitravelplanner.service;

import com.ai.aitravelplanner.dto.BudgetAnalysisDTO;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

import java.util.Map;

/**
 * 预算AI分析服务
 */
@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "openAiChatModel"
)
public interface BudgetAiService {

    @UserMessage("""
        你是一位专业的财务分析AI助手。
        用户将提供旅行行程的预算信息、总预算以及所有支出记录。
        请根据这些信息进行深度分析，包括：
        1. 消费趋势分析：分析支出变化趋势，识别消费模式
        2. 优化建议：基于当前支出情况，给出具体的预算优化建议
        3. 预算使用情况总结：总结当前预算使用情况
        4. 风险提示：如果支出过快或超支风险，给出预警
        
        输出格式必须严格遵循以下JSON结构：
        {
          "consumptionTrend": "消费趋势分析的详细文字描述",
          "suggestions": [
            "建议1：具体的优化建议内容",
            "建议2：具体的优化建议内容",
            "建议3：具体的优化建议内容"
          ],
          "budgetSummary": "预算使用情况的总结描述",
          "riskWarning": "风险提示信息，如果没有风险则写'暂无风险'"
        }
        
        请用中文回答，分析要专业、详细、具体，建议要可执行。
        
        用户预算数据：{{payload}}
    """)
    BudgetAnalysisDTO analyzeBudget(Map<String, Object> payload);
}


package com.apipulse.api_pulse_app.agent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentExecutionService {

    private final FinGuardAgent finGuardAgent;
    private final EscalationPolicyService escalationPolicyService;
    private final AgentLogService agentLogService;

    public AgentExecutionResult execute(String triggerType, String situation) {
        long startTime = System.currentTimeMillis();
        log.info("Agent 실행 시작 - type: {}", triggerType);

        escalationPolicyService.checkEscalation();

        String result = finGuardAgent.analyze(situation);
        long elapsed = System.currentTimeMillis() - startTime;

        // 하드코딩 제거 → 실제 결과에서 동적 추출
        String toolsUsed = extractToolsFromResult(result);

        AgentExecutionResult executionResult = AgentExecutionResult.builder()
                .triggerType(triggerType)
                .situation(situation)
                .result(result)
                .responseTimeMs(elapsed)
                .toolsUsed(toolsUsed)
                .build();

        agentLogService.save(executionResult);

        log.info("Agent 실행 완료 - {}ms, 사용 툴: {}", elapsed, toolsUsed);
        return executionResult;
    }

    private String extractToolsFromResult(String result) {
        List<String> tools = new ArrayList<>();
        if (result.contains("결제 지표") || result.contains("실패율"))
            tools.add("GetMetrics");
        if (result.contains("에러 로그") || result.contains("로그 검색"))
            tools.add("SearchLogs");
        if (result.contains("패턴 분석") || result.contains("실패 사유"))
            tools.add("AnalyzePattern");
        if (result.contains("복구") || result.contains("모드 해제"))
            tools.add("RestartService");
        if (result.contains("알림") || result.contains("발송"))
            tools.add("SendAlert");
        if (result.contains("리포트"))
            tools.add("GenerateReport");
        return tools.isEmpty() ? "없음" : String.join(", ", tools);
    }
}
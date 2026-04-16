package com.apipulse.api_pulse_app.agent.tools;

import com.apipulse.api_pulse_app.payment.PaymentService;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestartServiceTool {

    private final PaymentService paymentService;

    @Tool("카드 오류로 인한 결제 장애를 자동 복구합니다. " +
            "카드 오류 모드를 해제하고 최근 실패한 결제를 재시도합니다.")
    public String recoverFromCardError() {
        log.warn("[Tool] 자동복구 시작 - 카드 오류");

        try {
            // 1단계: 오류 모드 해제
            paymentService.setCardErrorMode(false);
            log.info("[Tool] 카드 오류 모드 해제 완료");

            // 2단계: 실패 결제 재시도
            PaymentService.RetryResult retryResult = paymentService.retryFailedPayments();
            log.info("[Tool] 결제 재시도 결과: {}", retryResult.message());

            return String.format("""
                === 카드 오류 자동복구 완료 ===
                1. 카드 오류 모드 해제 완료
                2. 실패 결제 재시도: %s
                상태: 정상화됨
                """, retryResult.message());

        } catch (Exception e) {
            log.error("[Tool] 자동복구 실패: {}", e.getMessage());
            return "자동복구 실패 - 수동 개입 필요: " + e.getMessage();
        }
    }

    @Tool("타임아웃으로 인한 결제 지연을 자동 복구합니다. " +
            "타임아웃 모드를 해제하고 최근 실패한 결제를 재시도합니다.")
    public String recoverFromTimeout() {
        log.warn("[Tool] 자동복구 시작 - 타임아웃");

        try {
            // 1단계: 타임아웃 모드 해제
            paymentService.setTimeoutMode(false);
            log.info("[Tool] 타임아웃 모드 해제 완료");

            // 2단계: 실패 결제 재시도
            PaymentService.RetryResult retryResult = paymentService.retryFailedPayments();
            log.info("[Tool] 결제 재시도 결과: {}", retryResult.message());

            return String.format("""
                === 타임아웃 자동복구 완료 ===
                1. 타임아웃 모드 해제 완료
                2. 실패 결제 재시도: %s
                상태: 정상화됨
                """, retryResult.message());

        } catch (Exception e) {
            log.error("[Tool] 자동복구 실패: {}", e.getMessage());
            return "자동복구 실패 - 수동 개입 필요: " + e.getMessage();
        }
    }

    @Tool("복구 가능 여부를 판단할 수 없을 때 현재 시스템 상태를 점검합니다.")
    public String checkSystemStatus() {
        log.info("[Tool] 시스템 상태 점검");

        boolean cardErrorMode = paymentService.isCardErrorMode();
        boolean timeoutMode = paymentService.isTimeoutMode();

        return String.format("""
            === 시스템 상태 점검 결과 ===
            카드 오류 모드: %s
            타임아웃 모드: %s
            권장 조치: %s
            """,
                cardErrorMode ? "활성화(장애 중)" : "비활성화(정상)",
                timeoutMode ? "활성화(장애 중)" : "비활성화(정상)",
                (cardErrorMode || timeoutMode) ? "복구 툴 실행 필요" : "추가 조치 불필요"
        );
    }
}
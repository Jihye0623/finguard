@echo off
chcp 65001 >nul
echo 결제 타임아웃 시나리오 시작...
curl -X POST http://localhost:8080/scenario/payment-timeout/on
echo 타임아웃 모드 ON - 결제 요청 시작...
for /L %%i in (1,1,5) do (
  echo %%i번째 지연 결제 요청...
  curl -s -X POST http://localhost:8080/payment/approve ^
    -H "Content-Type: application/json" ^
    -d "{\"cardNumber\":\"1234567890123456\",\"amount\":100000,\"merchantName\":\"이마트\"}"
)
curl -X POST http://localhost:8080/scenario/payment-timeout/off
echo 타임아웃 모드 OFF - 완료!
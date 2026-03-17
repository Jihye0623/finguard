@echo off
chcp 65001 >nul
echo 결제 폭증 시나리오 시작...
for /L %%i in (1,1,30) do (
  echo %%i번째 결제 요청...
  curl -s -X POST http://localhost:8080/payment/approve ^
    -H "Content-Type: application/json" ^
    -d "{\"cardNumber\":\"1234567890123456\",\"amount\":50000,\"merchantName\":\"스타벅스\"}"
)
echo 완료!
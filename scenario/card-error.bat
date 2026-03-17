@echo off
chcp 65001 >nul
echo 카드 오류 시나리오 시작...
curl -X POST http://localhost:8080/scenario/card-error/on
echo 카드 오류 모드 ON - 결제 요청 시작...
for /L %%i in (1,1,20) do (
  echo %%i번째 오류 결제 요청...
  curl -s -X POST http://localhost:8080/payment/approve ^
    -H "Content-Type: application/json" ^
    -d "{\"cardNumber\":\"9999999999999999\",\"amount\":200000,\"merchantName\":\"현대백화점\"}"
)
curl -X POST http://localhost:8080/scenario/card-error/off
echo 카드 오류 모드 OFF - 완료!
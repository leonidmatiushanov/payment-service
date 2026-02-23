curl -X POST http://localhost:8080/payments \
-H "Content-Type: application/json" \
-d '{
"inquiryRefId": "a1b2c3d4-e5f6-7890-abcd-1234567890ab",
"amount": 42.50,
"currency": "USD",
"status": "RECEIVED"
}'
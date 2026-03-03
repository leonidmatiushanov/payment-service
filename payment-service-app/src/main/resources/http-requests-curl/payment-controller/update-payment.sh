TOKEN=
curl --location \
--request PUT 'http://localhost:8080/payments/6d686ccc-c08e-4a02-969d-7ff3866d43ca' \
--header 'Content-Type: application/json' \
--header  "Authorization: Bearer $TOKEN" \
--data '{
    "amount": 20.20,
    "inquiryRefId": "ac328a1a-1e60-4dd3-bee5-ed542374c841",
    "currency": "RUB",
    "status": "APPROVED",
    "note": "note update",
    "createdAt": "2026-02-23T17:00:28.950757Z"
}'
TOKEN=
curl -H "Authorization: Bearer $TOKEN" \
-X GET 'http://localhost:8080/payments/filter?currency=EUR&minAmount=50&maxAmount=100&createdAfter=2023-01-27T01%3A35%3A00Z&createdBefore=2023-01-28T01%3A35%3A00Z'
TOKEN=
curl -H "Authorization: Bearer $TOKEN" \
-X GET 'http://localhost:8080/payments/page-search?sortBy=amount&direction=ask&page=0&size=30'
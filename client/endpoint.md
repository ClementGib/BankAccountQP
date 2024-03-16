curl --insecure --request POST \
-H "Content-Type: application/json" \
--url http://localhost:8080/transactions \
--data '{
"emitterAccountId": 12345,
"amount": 10,
"currency": "EUR",
"metadata": {}
}'

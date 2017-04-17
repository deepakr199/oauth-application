# authorization-service
Service helps to validate and generate refresh and access token. It uses play cache to put refresh and access token once created by setting appropriate ttl.

Following endpoints are exposed
1) Creates refresh token or access token based on grant_type in the request body
```
POST    /internal/v1/oauth/token
Payload for Grant Type PASSWORD ->{"grant_type":"PASSWORD","email":"valid@email.com","password":"somepass",
"client_id":"client-id-value"}
Payload for Grant Type REFRESH_TOKEN ->{"grant_type":"REFRESH_TOKEN","refresh_token":"refresh-token-value",
"client_id":"client-id-value"}
```

2) Validate the access token. This endpoint is used by resource-provider to validate the request from the client-service
```
GET     /internal/v1/authorized/:access_token 
```

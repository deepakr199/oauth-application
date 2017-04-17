# resource-provider
Service which serves/creates user data when requested from client-service

Following endpoints are exposed
1) Get users
```
GET     /internal/v1/user         
Headers -> access-token: {value}
```
2) Create user
```
POST    /internal/v1/user       
Payload -> {"phone":"12345","first_name": "Deepak","last_name": "R","email": "valid@email.com","password": "somepass","role":"ADMIN","status":"ACTIVE"}
Headers -> access-token: {value}
```

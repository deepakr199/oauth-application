# client-service
Service helps to handle user request on validating user credentials, creating access token, fetching users and create user

1) To Validate the credentials and get Refresh and Access token
```
POST    localhost:9998/v1/login  
Payload->{"email":"valid@email.com","password":"password"}
```
2) To get Access token from Refresh Token

```
POST    localhost:9998/v1/accessToken                
Header -> refresh-token:{value}
```

3) To get Users 

```
GET     localhost:9998/v1/user                
Header -> access-token:{value}
```

4) To save User

```
POST    localhost:9998/v1/user          
Header -> access-token:{value} 
Payload->{"phone":"12345","first_name": "Deepak","last_name": "R","email": "valid@email.com","password": "somepass","role":"ADMIN","status":"ACTIVE"}
```

# oauth-application
Oauth2 Resource Owner Password Credentials Grant with Refresh Token

Main Application consists of 3 sub projects which runs on its own
1) Authorization-Service
2) Client-Service
3) Resource-Provider

Run all sub projects on localhost to verify with commands
1) Authorization-Service -> `./activator "start 9999"`
2) Client-Service -> `./activator "start 9998"`
3) Resource-Provider -> `./activator "start 9997"`

On startup, resource-provider initializes mongo if there is no data and adds default user with ```email: valid@email.com password: password```

To test functionality, following endpoints are exposed

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

1) Validity of Refresh Token is 90 seconds
2) Validity of Access Token is 30 seconds

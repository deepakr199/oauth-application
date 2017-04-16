package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.response.AccessTokenResponse;
import models.request.LoginRequest;
import models.request.OauthRequest;
import models.response.UserListResponse;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.UnAuthorizedException;
import injector.JacksonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Configuration;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Http;

import java.util.Base64;
import javax.inject.Inject;
import javax.inject.Named;

public class ClientService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientService.class);

  private ObjectMapper objectMapper;
  private WSClient wsClient;
  private String validateCredentialsUrl;
  private String registerUserUrl;
  private String getUsersUrl;
  private String clientId;

  @Inject
  public ClientService(@Named(JacksonModule.CLIENT_JSON) ObjectMapper objectMapper, WSClient wsClient, Configuration
      configuration) {
    this.objectMapper = objectMapper;
    this.wsClient = wsClient;
    this.validateCredentialsUrl = configuration.getString("service.authorization.validate_credentials.endpoint");
    this.registerUserUrl = configuration.getString("service.user_resource.register_user.endpoint");
    this.getUsersUrl = configuration.getString("service.user_resource.get_users.endpoint");
    this.clientId = configuration.getString("oauth.client_id");
  }

  public AccessTokenResponse validateCredentials(LoginRequest loginRequest)
      throws JsonProcessingException, BadRequestException, UnAuthorizedException, NotFoundException {
    OauthRequest oauthRequest = new OauthRequest(loginRequest);
    oauthRequest.setPassword(generateEncodedPassword(loginRequest.getPassword()));
    oauthRequest.setGrantType(OauthRequest.GrantType.PASSWORD);
    oauthRequest.setClientId(clientId);
    return callValidCredentialsUrl(oauthRequest);
  }

  public AccessTokenResponse getAccessToken(String refreshToken)
      throws JsonProcessingException, UnAuthorizedException, BadRequestException, NotFoundException {
    OauthRequest oauthRequest = new OauthRequest();
    oauthRequest.setRefreshToken(refreshToken);
    oauthRequest.setGrantType(OauthRequest.GrantType.REFRESH_TOKEN);
    oauthRequest.setClientId(clientId);
    return callValidCredentialsUrl(oauthRequest);
  }

  public UserListResponse getUsers(String accessToken)
      throws UnAuthorizedException, JsonProcessingException, BadRequestException, NotFoundException {
    WSResponse wsResponse = wsClient.url(getUsersUrl).setHeader("access-token", accessToken).get().get(5000L);
    JsonNode jsonResponse = handleResponse(wsResponse);
    return objectMapper.treeToValue(jsonResponse, UserListResponse.class);
  }

  public void registerUser(UserListResponse.User userRequest, String accessToken)
      throws UnAuthorizedException, BadRequestException, JsonProcessingException, NotFoundException {
    userRequest.setPassword(generateEncodedPassword(userRequest.getPassword()));
    JsonNode jsonPayload = objectMapper.valueToTree(userRequest);
    WSResponse wsResponse = wsClient.url(registerUserUrl).setHeader("access-token", accessToken).post(jsonPayload).get(5000L);
    handleResponse(wsResponse);
  }

  private AccessTokenResponse callValidCredentialsUrl(OauthRequest oauthRequest)
      throws JsonProcessingException, UnAuthorizedException, BadRequestException, NotFoundException {
    JsonNode jsonPayload = objectMapper.valueToTree(oauthRequest);
    WSResponse wsResponse = wsClient.url(validateCredentialsUrl).post(jsonPayload).get(5000L);
    JsonNode jsonResponse = handleResponse(wsResponse);
    return objectMapper.treeToValue(jsonResponse, AccessTokenResponse.class);
  }

  private JsonNode handleResponse(WSResponse wsResponse)
      throws JsonProcessingException, UnAuthorizedException, BadRequestException, NotFoundException {
    JsonNode response = wsResponse.asJson();
    if (wsResponse.getStatus() == Http.Status.OK) {
      return response;
    } if (wsResponse.getStatus() == Http.Status.CREATED) {
      return response;
    } else if (wsResponse.getStatus() == Http.Status.BAD_REQUEST) {
      throw new BadRequestException("BadRequest: " + response.path("message").asText());
    } else if (wsResponse.getStatus() == Http.Status.UNAUTHORIZED) {
      throw new UnAuthorizedException("UnAuthorized Access: " + response.path("message").asText());
    } else if (wsResponse.getStatus() == Http.Status.NOT_FOUND) {
      throw new NotFoundException("Not Found: " + response.path("message").asText());
    } else {
      LOGGER.error("Unexpected Result: {}", response);
      throw new RuntimeException("UnExpected Result: " + response.path("message").asText());
    }
  }

  /**
   * Basic Password Encoder using Base64.
   */
  private String generateEncodedPassword(String password) {
    return Base64.getEncoder().encodeToString(password.getBytes());
  }


}

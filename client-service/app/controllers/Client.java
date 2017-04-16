package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.response.AccessTokenResponse;
import models.response.ClientResponse;
import models.request.LoginRequest;
import models.response.UserListResponse;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.UnAuthorizedException;
import injector.JacksonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.ClientService;

import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;

public class Client extends Controller {

  private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

  private ObjectMapper objectMapper;
  private ClientService clientService;

  @Inject
  public Client(@Named(JacksonModule.CLIENT_JSON) ObjectMapper objectMapper, ClientService clientService) {
    this.objectMapper = objectMapper;
    this.clientService = clientService;
  }

  public Result index() {
    return ok("Client Service up and running");
  }

  public Result login() {
    try {
      JsonNode jsonPayload = request().body().asJson();
      LoginRequest loginRequest = objectMapper.treeToValue(jsonPayload, LoginRequest.class);
      AccessTokenResponse accessTokenResponse = clientService.validateCredentials(loginRequest);
      JsonNode response = objectMapper.valueToTree(accessTokenResponse);
      return ok(response);
    } catch (Exception e) {
      return handleException(e);
    }
  }

  public Result getAccessToken() {
    try {
      Optional<String> refreshToken = Optional.ofNullable(request().getHeader("refresh-token"));
      if (!refreshToken.isPresent()) {
        return badRequest(generateResponse("Missing header RefreshToken"));
      }
      AccessTokenResponse accessTokenResponse = clientService.getAccessToken(refreshToken.get());
      JsonNode response = objectMapper.valueToTree(accessTokenResponse);
      return ok(response);
    } catch (Exception e) {
      return handleException(e);
    }
  }

  public Result getUsers() {
    try {
      Optional<String> accessToken = Optional.ofNullable(request().getHeader("access-token"));
      if (!accessToken.isPresent()) {
        return badRequest(generateResponse("Missing header AccessToken"));
      }
      LOGGER.info("Fetching Users...");
      UserListResponse userListResponse = clientService.getUsers(accessToken.get());
      LOGGER.info("User list with count: {}", userListResponse.getCount());
      JsonNode response = objectMapper.valueToTree(userListResponse);
      return ok(response);
    } catch (Exception e) {
      return handleException(e);
    }
  }

  public Result createUser() {
    try {
      Optional<String> accessToken = Optional.ofNullable(request().getHeader("access-token"));
      if (!accessToken.isPresent()) {
        return badRequest(generateResponse("Missing header AccessToken"));
      }
      JsonNode jsonPayload = request().body().asJson();
      UserListResponse.User userRequest = objectMapper.treeToValue(jsonPayload, UserListResponse.User.class);
      LOGGER.info("Creating User with email: {}", userRequest.getEmail());
      clientService.registerUser(userRequest, accessToken.get());
      return ok(generateResponse("Created User"));
    } catch (Exception e) {
      return handleException(e);
    }
  }

  private JsonNode generateResponse(String message) {
    return objectMapper.valueToTree(new ClientResponse(message));
  }

  private Result handleException(Exception e) {
    int status;
    if (e instanceof BadRequestException) {
      status = Http.Status.BAD_REQUEST;
    } else if (e instanceof UnAuthorizedException) {
      status = Http.Status.UNAUTHORIZED;
    } else if (e instanceof NotFoundException) {
      status = Http.Status.NOT_FOUND;
    } else {
      status = Http.Status.INTERNAL_SERVER_ERROR;
    }
    LOGGER.error("Oops. Something went wrong", e);
    return status(status, generateResponse(e.getMessage()));
  }
}

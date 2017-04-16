package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.request.UserRequest;
import helper.Secured;
import injector.JacksonModule;
import models.response.UserListResponse;
import models.response.UserResourceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import services.UserResourceService;

import javax.inject.Inject;
import javax.inject.Named;

public class UserResource extends Controller {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

  private ObjectMapper objectMapper;
  private UserResourceService userResourceService;

  @Inject
  public UserResource(@Named(JacksonModule.USER_RESOURCE_JSON) ObjectMapper objectMapper, UserResourceService userResourceService) {
    this.objectMapper = objectMapper;
    this.userResourceService = userResourceService;
  }

  public Result index() {
    return ok("User Resource Service up and running");
  }

  @Security.Authenticated(Secured.class)
  public Result getUsers() {
    try {
      LOGGER.info("Listing users");
      UserListResponse userListResponse = userResourceService.getUsers();
      LOGGER.info("Found users with count: {}", userListResponse.getCount());
      JsonNode response = objectMapper.valueToTree(userListResponse);
      return ok(response);
    } catch(Exception e) {
      LOGGER.error("Exception while fetching user lists", e);
      return internalServerError(generateResponse(e.getMessage()));
    }
  }

  @Security.Authenticated(Secured.class)
  public Result createUser() {
    try {
      JsonNode jsonPayload = request().body().asJson();
      UserRequest userRequest = objectMapper.treeToValue(jsonPayload, UserRequest.class);
      LOGGER.info("Request to create User with email: {}", userRequest.getEmail());
      Long id = userResourceService.saveUser(userRequest);
      return created(generateResponse("Created User with Id: " + id));
    } catch(Exception e) {
      LOGGER.error("Exception while saving user", e);
      return internalServerError(generateResponse(e.getMessage()));
    }
  }

  private JsonNode generateResponse(String message) {
    return objectMapper.valueToTree(new UserResourceResponse(message));
  }
}

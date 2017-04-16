package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.response.AuthorizationResponse;
import models.request.OauthRequest;
import models.response.TokenVO;
import exceptions.InValidCredentialException;
import exceptions.RefreshTokenNotFoundException;
import injector.JacksonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.AuthorizationService;

import javax.inject.Inject;
import javax.inject.Named;

public class Authorization extends Controller {

  private static final Logger LOGGER = LoggerFactory.getLogger(Authorization.class);

  private AuthorizationService authorizationService;
  private ObjectMapper objectMapper;

  @Inject
  public Authorization(AuthorizationService authorizationService,
                       @Named(JacksonModule.AUTHORIZATION_JSON) ObjectMapper objectMapper) {
    this.authorizationService = authorizationService;
    this.objectMapper = objectMapper;
  }

  public Result index() throws InterruptedException {
    return ok(Json.toJson("Authorization Service up and running"));
  }

  public Result oauthToken() {
    try {
      JsonNode jsonPayload = request().body().asJson();
      LOGGER.info("Oauth Token Request: {}", jsonPayload);

      OauthRequest oauthRequest = objectMapper.treeToValue(jsonPayload, OauthRequest.class);
      TokenVO tokenValue = authorizationService.processOauthAuth(oauthRequest);
      JsonNode response = objectMapper.valueToTree(tokenValue);
      return ok(response);
    } catch (RefreshTokenNotFoundException | InValidCredentialException e) {
      LOGGER.info("Exception when requesting Oauth Token", e);
      return badRequest(generateResponse(e.getMessage()));
    } catch (Exception e) {
      LOGGER.info("Exception when requesting Oauth Token", e);
      return internalServerError(generateResponse(e.getMessage()));
    }
  }

  public Result authorized(String accessToken) {
    LOGGER.info("Validate Access Token Request: {}", accessToken);
    if(authorizationService.isAuthorized(accessToken)) {
      return ok();
    }
    LOGGER.warn("InValid AccessToken: {}", accessToken);
    return unauthorized();
  }

  private JsonNode generateResponse(String message) {
    return objectMapper.valueToTree(new AuthorizationResponse(message));
  }
}

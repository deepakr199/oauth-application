package helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import injector.JacksonModule;
import models.response.UserResourceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Configuration;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.util.Optional;
import javax.inject.Named;

/**
 * The Class Secured. Authorization class gives permission for user actions.
 */
public class Secured extends Security.Authenticator {

  private static final Logger LOGGER = LoggerFactory.getLogger(Secured.class);

  private ObjectMapper objectMapper;
  private WSClient wsClient;
  private String authorizedUrl;
  private String accessTokenPlaceHolder;

  @Inject
  public Secured(@Named(JacksonModule.USER_RESOURCE_JSON) ObjectMapper objectMapper,
                 WSClient wsClient, Configuration configuration) {
    this.objectMapper = objectMapper;
    this.wsClient = wsClient;
    this.authorizedUrl = configuration.getString("service.authorization.url");
    this.accessTokenPlaceHolder = configuration.getString("access_token.placeholder");
  }

  /*
   * (non-Javadoc)
   *
   * @see play.mvc.Security.Authenticator#getUsername(play.mvc.Http.Context)
   */
  @Override
  public String getUsername(Http.Context ctx) {
    if (isAuthorized(ctx)) {
      return "true";
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see play.mvc.Security.Authenticator#onUnauthorized(play.mvc.Http.Context)
   */
  @Override
  public Result onUnauthorized(Http.Context ctx) {
    JsonNode response =  objectMapper.valueToTree(new UserResourceResponse("UnAuthorized Access Token"));
    return unauthorized(response);
  }

  /**
   * Checks if is authorized.
   *
   * @param ctx
   *            the ctx
   * @return true, if is authorized
   */
  private boolean isAuthorized(Http.Context ctx) {
    Optional<String> accessToken = Optional.ofNullable(ctx.request().getHeader("access-token"));
    if (accessToken.isPresent()) {
      WSResponse wsResponse = wsClient
          .url(authorizedUrl.replace(accessTokenPlaceHolder, accessToken.get()))
          .get().get(5000L);
      if (wsResponse.getStatus() == Http.Status.OK) {
        return true;
      }
    }
    LOGGER.info("Unauthorized request with AccessToken: {}", accessToken);
    return false;
  }
}
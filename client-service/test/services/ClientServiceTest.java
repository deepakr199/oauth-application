package services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.request.LoginRequest;
import models.response.AccessTokenResponse;
import models.response.UserListResponse;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.UnAuthorizedException;
import injector.JacksonModule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Configuration;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.mvc.Http;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private ClientService clientService;

  private ObjectMapper objectMapper = new JacksonModule().configureObjectMapper();

  @Mock
  private Configuration configurationMock;
  @Mock
  private WSClient wsClientMock;
  @Mock
  private WSRequest wsRequestMock;
  @Mock
  private F.Promise<WSResponse> wsResponsePromiseMock;
  @Mock
  private WSResponse wsResponseMock;

  @Before
  public void setUp() {
    setUpWsClient();
    setUpConfiguration();
    clientService = new ClientService(objectMapper, wsClientMock, configurationMock);
  }

  private void setUpConfiguration() {
    when(configurationMock.getString("service.authorization.validate_credentials.endpoint")).thenReturn("authorization");
    when(configurationMock.getString("service.user_resource.register_user.endpoint")).thenReturn("registerUser");
    when(configurationMock.getString("service.user_resource.get_users.endpoint")).thenReturn("getUser");
    when(configurationMock.getString("oauth.client_id")).thenReturn("clientId");
  }

  private void setUpWsClient() {
      when(wsClientMock.url(any()))
          .thenReturn(wsRequestMock);
      when(wsRequestMock.setHeader("access-token", "valid-access-token"))
          .thenReturn(wsRequestMock);
      when(wsRequestMock.post(any(JsonNode.class)))
          .thenReturn(wsResponsePromiseMock);
      when(wsRequestMock.get())
        .thenReturn(wsResponsePromiseMock);
      when(wsResponsePromiseMock.get(anyLong()))
          .thenReturn(wsResponseMock);
  }

  @Test
  public void validateCredentialsOKTest() throws Exception {
    when(wsResponseMock.asJson()).thenReturn(objectMapper.valueToTree(AccessTokenResponse.builder()
        .accessToken("valid-access-token").refreshToken("valid-refresh-token")
        .build()));
    when(wsResponseMock.getStatus()).thenReturn(Http.Status.OK);
    LoginRequest loginRequest = new LoginRequest("valid@email.com", "validPassword");

    AccessTokenResponse accessTokenResponse = clientService.validateCredentials(loginRequest);

    assertEquals("valid-access-token", accessTokenResponse.getAccessToken());
    assertEquals("valid-access-token", accessTokenResponse.getAccessToken());
  }

  @Test
  public void validateCredentialsBadRequestTest() throws Exception {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Bad Request");

    when(wsResponseMock.asJson()).thenReturn(Json.newObject().put("message", "Bad Request"));
    when(wsResponseMock.getStatus()).thenReturn(Http.Status.BAD_REQUEST);

    LoginRequest loginRequest = new LoginRequest("badrequest@email.com", "badrequestPassword");
    clientService.validateCredentials(loginRequest);
  }

  @Test
  public void validateCredentialsUnAuthorizedTest() throws Exception {
    expectedException.expect(UnAuthorizedException.class);
    expectedException.expectMessage("UnAuthorized Request");

    when(wsResponseMock.asJson()).thenReturn(Json.newObject().put("message", "UnAuthorized Request"));
    when(wsResponseMock.getStatus()).thenReturn(Http.Status.UNAUTHORIZED);

    LoginRequest loginRequest = new LoginRequest("unAuthorized@email.com", "unAuthorizedPassword");
    clientService.validateCredentials(loginRequest);
  }

  @Test
  public void validateCredentialsServerErrorTest() throws Exception {
    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("Internal Server Error Request");

    when(wsResponseMock.asJson()).thenReturn(Json.newObject().put("message", "Internal Server Error Request"));
    when(wsResponseMock.getStatus()).thenReturn(Http.Status.INTERNAL_SERVER_ERROR);

    LoginRequest loginRequest = new LoginRequest("serverError@email.com", "serverErrorPassword");
    clientService.validateCredentials(loginRequest);
  }

  @Test
  public void getAccessTokenOKTest() throws Exception {
    when(wsResponseMock.asJson()).thenReturn(objectMapper.valueToTree(AccessTokenResponse.builder()
        .accessToken("valid-access-token").refreshToken("valid-refresh-token")
        .build()));
    when(wsResponseMock.getStatus()).thenReturn(Http.Status.OK);

    AccessTokenResponse accessTokenResponse = clientService.getAccessToken("valid-access-token");

    assertEquals("valid-access-token", accessTokenResponse.getAccessToken());
    assertEquals("valid-access-token", accessTokenResponse.getAccessToken());
  }

  @Test
  public void getUsersOKTest() throws Exception {
    when(wsResponseMock.asJson()).thenReturn(objectMapper.valueToTree(UserListResponse.builder()
        .users(new ArrayList<>()).count(5)
        .build()));
    when(wsResponseMock.getStatus()).thenReturn(Http.Status.OK);

    UserListResponse userListResponse = clientService.getUsers("valid-access-token");

    assertEquals(5, userListResponse.getCount().intValue());
  }

  @Test
  public void getUsersNotFoundTest() throws Exception {
    expectedException.expect(NotFoundException.class);
    expectedException.expectMessage("Not Found");

    when(wsResponseMock.asJson()).thenReturn(Json.newObject().put("message", "Not Found"));
    when(wsResponseMock.getStatus()).thenReturn(Http.Status.NOT_FOUND);

    clientService.getUsers("valid-access-token");
  }

  @Test
  public void getUserCreatedTest() throws Exception {
    when(wsResponseMock.getStatus()).thenReturn(Http.Status.CREATED);

    UserListResponse.User user = UserListResponse.User.builder().password("somepassword").build();
    clientService.registerUser(user, "valid-access-token");
  }


}

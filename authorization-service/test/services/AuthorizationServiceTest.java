package services;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dao.AuthorizationDao;
import models.request.OauthRequest;
import models.response.TokenVO;
import enums.GrantType;
import enums.UserRole;
import exceptions.InValidCredentialException;
import exceptions.RefreshTokenNotFoundException;
import helpers.TokenCache;
import models.User;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private AuthorizationService authorizationService;

  @Mock
  private AuthorizationDao authorizationDaoMock;
  @Mock
  private TokenCache tokenCacheMock;

  @Before
  public void setUp() {
    authorizationService = new AuthorizationService(authorizationDaoMock, tokenCacheMock);
  }

  @Test(expected = InValidCredentialException.class)
  public void processOauthAuthInValidCredentialsTest() throws Exception {
    OauthRequest oauthRequest = OauthRequest.builder()
        .clientId("clientId").email("valid@email.com")
        .password("validPassword").grantType(GrantType.PASSWORD)
        .build();
    authorizationService.processOauthAuth(oauthRequest);

    verify(tokenCacheMock).generateRefreshToken(new TokenVO.UserProfile(eq("valid@email.com"), eq(UserRole.ADMIN)));
    verify(authorizationDaoMock).validateUserCredentials(eq("valid@email.com"), eq("validPassword"));
  }

  @Test
  public void processOauthAuthPasswordGrantTest() throws Exception {
    when(authorizationDaoMock.validateUserCredentials(eq("valid@email.com"), eq("validPassword")))
        .thenReturn(User.builder().email("valid@email.com").role(UserRole.ADMIN).build());
    when(tokenCacheMock.generateRefreshToken(any()))
        .thenReturn(TokenVO.builder().accessToken("access-token-value").build());

    OauthRequest oauthRequest = OauthRequest.builder()
        .clientId("clientId").email("valid@email.com")
        .password("validPassword").grantType(GrantType.PASSWORD)
        .build();
    authorizationService.processOauthAuth(oauthRequest);

    verify(tokenCacheMock).generateRefreshToken(any());
    verify(authorizationDaoMock).validateUserCredentials(eq("valid@email.com"), eq("validPassword"));
  }

  @Test(expected = RefreshTokenNotFoundException.class)
  public void processOauthAuthRefreshTokenNotFoundTest() throws Exception {
    when(tokenCacheMock.getRefreshToken(any())).thenThrow(RefreshTokenNotFoundException.class);

    OauthRequest oauthRequest = OauthRequest.builder()
        .grantType(GrantType.REFRESH_TOKEN).build();
    authorizationService.processOauthAuth(oauthRequest);

    verify(tokenCacheMock).getRefreshToken(anyString());
    verify(tokenCacheMock, never()).generateAccessToken(any(), anyString());
    verify(tokenCacheMock, never()).generateRefreshToken(any());
    verify(authorizationDaoMock, never());
  }

  @Test
  public void processOauthAuthRefreshTokenTest() throws Exception {
    when(tokenCacheMock.getRefreshToken(any())).thenReturn(TokenVO.builder()
        .accessToken("access-token-value").refreshToken("refresh-token-value")
        .userProfile(new TokenVO.UserProfile("valid@email.com", UserRole.ADMIN))
        .build());
    when(tokenCacheMock.generateAccessToken(any(), anyString())).thenReturn(TokenVO.builder()
        .accessToken("access-token-value").refreshToken("refresh-token-value")
        .userProfile(new TokenVO.UserProfile("valid@email.com", UserRole.ADMIN))
        .build());

    OauthRequest oauthRequest = OauthRequest.builder()
        .grantType(GrantType.REFRESH_TOKEN).build();
    authorizationService.processOauthAuth(oauthRequest);

    verify(tokenCacheMock).getRefreshToken(anyString());
    verify(tokenCacheMock).generateAccessToken(any(), anyString());
    verify(tokenCacheMock, never()).generateRefreshToken(any());
  }
}

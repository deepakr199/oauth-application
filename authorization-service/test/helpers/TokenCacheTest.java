package helpers;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import models.response.TokenVO;
import enums.UserRole;
import exceptions.RefreshTokenNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Configuration;
import play.cache.CacheApi;

@RunWith(MockitoJUnitRunner.class)
public class TokenCacheTest {

  private TokenCache tokenCache;

  @Mock
  private Configuration configurationMock;

  @Mock
  private CacheApi cacheApiMock;

  @Captor
  ArgumentCaptor<TokenVO> argumentCaptor;

  @Before
  public void setUp() {
    when(configurationMock.getInt("expiration.access.token")).thenReturn(5);
    when(configurationMock.getInt("expiration.refresh.token")).thenReturn(10);
    tokenCache = new TokenCache(cacheApiMock, configurationMock);
  }

  @Test
  public void generateRefreshTokenTest() {
    TokenVO.UserProfile userProfile = new TokenVO.UserProfile("deepak@gmail.com", UserRole.ADMIN);
    TokenVO tokenVOResult = tokenCache.generateRefreshToken(userProfile);

    verify(cacheApiMock, times(1)).set(anyString(), argumentCaptor.capture(), eq(5));

    TokenVO tokenVO = argumentCaptor.getValue();
    Assert.assertEquals(tokenVO.getAccessToken(), tokenVOResult.getAccessToken());
    Assert.assertEquals(tokenVO.getRefreshToken(), tokenVOResult.getRefreshToken());
    Assert.assertEquals(tokenVO.getUserProfile().getEmail(), tokenVOResult.getUserProfile().getEmail());
    Assert.assertEquals(tokenVO.getUserProfile().getEmail(), tokenVOResult.getUserProfile().getEmail());

    verify(cacheApiMock, times(1)).set(anyString(), argumentCaptor.capture(), eq(10));
  }

  @Test
  public void generateAccessTokenTokenTest() {
    TokenVO.UserProfile userProfile = new TokenVO.UserProfile("deepak@gmail.com", UserRole.ADMIN);
    TokenVO tokenVOResult = tokenCache.generateAccessToken(userProfile, "refresh-token-value");

    verify(cacheApiMock, times(1)).set(anyString(), argumentCaptor.capture(), eq(5));

    TokenVO tokenVO = argumentCaptor.getValue();
    Assert.assertEquals(tokenVO.getAccessToken(), tokenVOResult.getAccessToken());
    Assert.assertEquals(tokenVO.getRefreshToken(), tokenVOResult.getRefreshToken());
    Assert.assertEquals(tokenVO.getUserProfile().getEmail(), tokenVOResult.getUserProfile().getEmail());
    Assert.assertEquals(tokenVO.getUserProfile().getEmail(), tokenVOResult.getUserProfile().getEmail());
  }

  @Test
  public void getRefreshTokenTest() throws Exception {
    when(cacheApiMock.get("refresh-token-value")).thenReturn(new TokenVO());
    tokenCache.getRefreshToken("refresh-token-value");
    verify(cacheApiMock, times(1)).get(eq("refresh-token-value"));
  }

  @Test(expected = RefreshTokenNotFoundException.class)
  public void getRefreshTokenNotFoundTest() throws Exception {
    tokenCache.getRefreshToken("refresh-token-value");
    verify(cacheApiMock, times(1)).get(eq("refresh-token-value"));
  }

  @Test
  public void isValidAccessTokenTest() throws Exception {
    when(cacheApiMock.get("access-token-value")).thenReturn(new TokenVO());
    Assert.assertTrue(tokenCache.isValidAccessToken("access-token-value"));
    verify(cacheApiMock, times(1)).get(eq("access-token-value"));
  }

  @Test
  public void isValidAccessTokenFailTest() throws Exception {
    Assert.assertFalse(tokenCache.isValidAccessToken("access-token-value"));
    verify(cacheApiMock, times(1)).get(eq("access-token-value"));
  }


}

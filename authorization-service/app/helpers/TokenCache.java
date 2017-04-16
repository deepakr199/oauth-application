package helpers;

import models.response.TokenVO;
import exceptions.RefreshTokenNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Configuration;
import play.cache.CacheApi;

import java.util.UUID;
import javax.inject.Inject;

public class TokenCache {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenCache.class);

  private CacheApi cacheApi;
  private Integer accessTokenExpiration;
  private Integer refreshTokenExpiration;

  @Inject
  public TokenCache(CacheApi cacheApi, Configuration configuration) {
    this.cacheApi = cacheApi;
    this.accessTokenExpiration = configuration.getInt("expiration.access.token");
    this.refreshTokenExpiration = configuration.getInt("expiration.refresh.token");
  }

  public TokenVO generateAccessToken(TokenVO.UserProfile userProfile, String refreshToken) {
    String accessToken = UUID.randomUUID().toString();
    TokenVO accessTokenVO = new TokenVO(refreshToken, accessToken, userProfile);
    cacheApi.set(accessToken, accessTokenVO, accessTokenExpiration);
    LOGGER.info("Created AccessToken and Added to Cache. AccessToken: {} with expiration: {} for RefreshToken: {}",
        accessToken, accessTokenExpiration, refreshToken);
    return accessTokenVO;
  }

  public TokenVO generateRefreshToken(TokenVO.UserProfile userProfile) {
    String refreshToken = UUID.randomUUID().toString();
    TokenVO accessTokenVO = generateAccessToken(userProfile, refreshToken);
    TokenVO refreshTokenVO = new TokenVO(refreshToken, accessTokenVO.getAccessToken(), userProfile);
    cacheApi.set(refreshToken, refreshTokenVO, refreshTokenExpiration);
    LOGGER.info("Created RefreshToken and Added to Cache. RefreshToken: {} with expiration: {} and AccessToken: {}",
        refreshToken, refreshTokenExpiration, accessTokenVO.getAccessToken());
    return refreshTokenVO;
  }

  public boolean isValidAccessToken(String accessToken) {
    if (cacheApi.get(accessToken) != null) {
      return true;
    }
    return false;
  }

  public TokenVO getRefreshToken(String refreshToken) throws RefreshTokenNotFoundException {
    TokenVO refreshTokenVO = cacheApi.get(refreshToken);
    if (refreshTokenVO == null) {
      LOGGER.error("No Refresh Token found: {}", refreshToken);
      throw new RefreshTokenNotFoundException("RefreshToken not Found. Please try creating another one...");
    }
    return refreshTokenVO;
  }

  public void removeAccessToken(String accesstoken) {
    cacheApi.remove(accesstoken);
  }

}

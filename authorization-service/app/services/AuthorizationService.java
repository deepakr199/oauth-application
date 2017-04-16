package services;

import dao.AuthorizationDao;
import models.request.OauthRequest;
import models.response.TokenVO;
import enums.GrantType;
import exceptions.InValidCredentialException;
import exceptions.RefreshTokenNotFoundException;
import helpers.TokenCache;
import models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class AuthorizationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationService.class);

  private AuthorizationDao authorizationDao;
  private TokenCache tokenCache;

  @Inject
  public AuthorizationService(AuthorizationDao authorizationDao, TokenCache tokenCache) {
    this.authorizationDao = authorizationDao;
    this.tokenCache = tokenCache;
  }

  public TokenVO processOauthAuth(OauthRequest oauthRequest) throws RefreshTokenNotFoundException, InValidCredentialException {
    GrantType requestedGrantType = oauthRequest.getGrantType();

    if(GrantType.PASSWORD.equals(requestedGrantType)) {
      return handlePasswordGrantType(oauthRequest);
    } else if (GrantType.REFRESH_TOKEN.equals(requestedGrantType)) {
      return handleRefreshGrantType(oauthRequest);
    }
    throw new RuntimeException();
  }

  public boolean isAuthorized(String accessToken) {
    return tokenCache.isValidAccessToken(accessToken);
  }

  private TokenVO handlePasswordGrantType(OauthRequest oauthRequest) throws InValidCredentialException {
    User user = authorizationDao.validateUserCredentials(oauthRequest.getEmail(), oauthRequest.getPassword());
    if (user == null) {
      throw new InValidCredentialException("Invalid Credentials");
    }
    TokenVO.UserProfile userProfile = new TokenVO.UserProfile(user.getEmail(), user.getRole());
    TokenVO accessTokenVO = tokenCache.generateRefreshToken(userProfile);
    LOGGER.info("Created Access Token: {} for Grant Type: {}", accessTokenVO.getAccessToken(), oauthRequest.getGrantType());
    return accessTokenVO;
  }

  private TokenVO handleRefreshGrantType(OauthRequest oauthRequest) throws RefreshTokenNotFoundException {
    TokenVO refreshTokenVO = tokenCache.getRefreshToken(oauthRequest.getRefreshToken());
    TokenVO accessTokenVO = tokenCache.generateAccessToken(refreshTokenVO.getUserProfile(), refreshTokenVO.getRefreshToken());
    tokenCache.removeAccessToken(refreshTokenVO.getAccessToken());
    LOGGER.info("Created Access Token: {} for Grant Type: {}", accessTokenVO.getAccessToken(), oauthRequest.getGrantType());
    return accessTokenVO;
  }

}

package models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OauthRequest {

  @JsonProperty("grant_type")
  private GrantType grantType;

  private String email;
  private String password;

  @JsonProperty("client_id")
  private String clientId;

  @JsonProperty("refresh_token")
  private String refreshToken;

  public OauthRequest(LoginRequest loginRequest) {
    this.email = loginRequest.getEmail();
    this.password = loginRequest.getPassword();
  }

  public enum GrantType {
    PASSWORD,
    REFRESH_TOKEN
  }

}

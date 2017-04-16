package models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import enums.GrantType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
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

}

package models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenVO {

  @JsonProperty("refresh_token")
  private String refreshToken;

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("user_profile")
  private UserProfile userProfile;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class UserProfile {
    private String email;
    private UserRole role;
  }

}

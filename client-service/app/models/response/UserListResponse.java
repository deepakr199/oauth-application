package models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListResponse {

  private List<User> users;
  private Integer count;

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class User {
    private Long id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String email;
    private String password;
    private String phone;
    private UserStatus status;
    private UserRole role;

    public enum UserStatus {
      ACTIVE,
      INACTIVE
    }

    public enum UserRole {
      ADMIN,
      DEVELOPER
    }

  }
}

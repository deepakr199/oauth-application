package models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.User;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

  @JsonProperty("first_name")
  private String firstName;

  @JsonProperty("last_name")
  private String lastName;

  private String email;
  private String password;
  private String phone;
  private User.UserStatus status;
  private User.UserRole role;

}

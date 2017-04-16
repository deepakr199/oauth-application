package models.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.User;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListResponse {

  private List<User> users;
  private Integer count;

}

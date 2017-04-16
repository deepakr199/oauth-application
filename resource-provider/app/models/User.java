package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.request.UserRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;

@Data
@Entity(value = "users", noClassnameStored = true)
@AllArgsConstructor
@NoArgsConstructor
public class User {

  @Id
  private ObjectId _id;
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

  @JsonProperty("created_at")
  private Date createdAt;

  @JsonProperty("updated_at")
  private Date updatedAt;

  public User(UserRequest userRequest) {
    this.firstName = userRequest.getFirstName();
    this.lastName = userRequest.getLastName();
    this.email = userRequest.getEmail();
    this.phone = userRequest.getPhone();
    this.password = userRequest.getPassword();
    this.role = userRequest.getRole();
    this.status = userRequest.getStatus();
  }

  public enum UserStatus {
    ACTIVE,
    INACTIVE
  }

  public enum UserRole {
    ADMIN,
    DEVELOPER
  }

}

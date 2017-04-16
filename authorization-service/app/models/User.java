package models;

import enums.UserRole;
import enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "users", noClassnameStored = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

  @Id
  private ObjectId _id;

  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private UserRole role;
  private UserStatus status;

}

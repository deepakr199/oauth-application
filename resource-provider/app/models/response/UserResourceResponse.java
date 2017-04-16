package models.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class UserResourceResponse {

  private String message;
  //private Integer code; //Incase if we need to decode and handle it differently with same status code

}

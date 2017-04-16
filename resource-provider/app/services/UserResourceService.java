package services;

import com.google.inject.Inject;
import dao.CounterDao;
import dao.UserResourceDao;
import models.request.UserRequest;
import models.User;
import models.response.UserListResponse;

import java.util.Date;
import java.util.List;

public class UserResourceService {

  private UserResourceDao userResourceDao;
  private CounterDao counterDao;

  @Inject
  public UserResourceService(UserResourceDao userResourceDao, CounterDao counterDao) {
    this.userResourceDao = userResourceDao;
    this.counterDao = counterDao;
  }

  public UserListResponse getUsers() {
    List<User> users = userResourceDao.getUsers();
    return new UserListResponse(users, users.size());
  }

  public Long saveUser(UserRequest userRequest) {
    User user = new User(userRequest);
    user.setCreatedAt(new Date());
    user.setId(counterDao.getNextId("users"));
    return userResourceDao.saveUser(user);
  }
}

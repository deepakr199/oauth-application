package dao;

import lombok.extern.slf4j.Slf4j;
import models.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import javax.inject.Inject;

@Slf4j
public class AuthorizationDao extends BasicDAO<User, ObjectId> {

  @Inject
  public AuthorizationDao(Datastore datastore) {
    super(datastore);
  }

  public User validateUserCredentials(String email, String password) {
    Query<User> query = getDs().createQuery(User.class);
    query.field("email").equal(email);
    query.field("password").equal(password);
    User user = query.get();
    return user;
  }
}

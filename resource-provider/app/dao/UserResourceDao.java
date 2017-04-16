package dao;

import com.google.inject.Inject;
import models.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import java.util.List;

public class UserResourceDao extends BasicDAO<User, ObjectId> {

  @Inject
  public UserResourceDao(Datastore datastore) {
    super(datastore);
  }

  public List<User> getUsers() {
    Query<User> userQuery = getDs().createQuery(User.class).retrievedFields(false, "password");
    return userQuery.asList();
  }

  public Long saveUser(User user) {
    getDs().save(user);
    return user.getId();
  }

}

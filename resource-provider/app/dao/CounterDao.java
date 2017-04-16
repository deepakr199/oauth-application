package dao;

import com.google.inject.Inject;
import com.mongodb.operation.UpdateOperation;
import models.Counter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

public class CounterDao extends BasicDAO<Counter, ObjectId> {

  @Inject
  public CounterDao(Datastore datastore) {
    super(datastore);
  }

  public Long getNextId(String collection) {
    Query<Counter> counterQuery = getDs().createQuery(Counter.class);
    counterQuery.field("collection").equal(collection);
    UpdateOperations<Counter> updateOperations = getDs().createUpdateOperations(Counter.class);
    updateOperations.inc("seq");
    Counter counter = getDs().findAndModify(counterQuery, updateOperations, false, true);
    return counter.getSeq();
  }
}

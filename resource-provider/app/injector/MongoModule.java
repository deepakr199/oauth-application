package injector;


import com.google.inject.AbstractModule;
import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.logging.MorphiaLoggerFactory;
import play.Configuration;
import play.Environment;

/**
 *  Module to help configure and connect to mongo
 */
public class MongoModule extends AbstractModule {

  private final String mongoHost;
  private final String mongoPort;
  private final String dbName;

  private Datastore datastore;
  private MongoClient mongo;
  private Morphia morphia;

  /**
   * Morphia Google Guice Module used to be able to inject Morphia and Datastore instances elsewhere.
   */
  public MongoModule(Environment environment, Configuration configuration) {
    mongoHost = configuration.getString("mongodb.host", "mongohost");
    mongoPort = configuration.getString("mongodb.port", "27017");
    dbName = configuration.getString("mongodb.db", "redmart");
  }

  /**
   * Invoked by Guice during bootstrap.
   */
  @Override
  protected void configure() {
    MorphiaLoggerFactory.reset();
    init();
    bind();
  }

  private void init() {
    mongo = createMongoClient();
    morphia = new Morphia();
    datastore = createDataStore();
  }

  private void bind() {
    bind(Morphia.class).toInstance(morphia);
    bind(Datastore.class).toInstance(datastore);
  }

  private MongoClient createMongoClient() {
    ServerAddress serverAddress = new ServerAddress(mongoHost, Integer.parseInt(mongoPort));
    MongoClient mongoClient = new MongoClient(serverAddress);
    mongoClient.setReadPreference(ReadPreference.secondaryPreferred());
    return mongoClient;
  }

  private Datastore createDataStore() {
    Datastore datastore = morphia.createDatastore(mongo, dbName);
    datastore.ensureIndexes();
    datastore.ensureCaps();
    datastore.setDefaultWriteConcern(WriteConcern.ACKNOWLEDGED);
    return datastore;
  }
}
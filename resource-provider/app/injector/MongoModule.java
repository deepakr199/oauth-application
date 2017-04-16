package injector;


import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import models.User;
import models.request.UserRequest;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.logging.MorphiaLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Configuration;
import play.Environment;
import services.UserResourceService;

import java.util.Base64;

/**
 *  Module to help configure and connect to mongo
 */
public class MongoModule extends AbstractModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoModule.class);

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
    supplyInitialData();
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

  private void supplyInitialData() {
    bind(MongoDataInitializer.class).asEagerSingleton();
  }

  public static class MongoDataInitializer {

    @Inject
    public MongoDataInitializer(UserResourceService userResourceService) {
      if (userResourceService.getUsers().getCount() == 0) {
        LOGGER.info("Loading Data into mongo...");
        UserRequest userRequest = UserRequest.builder()
            .firstName("Deepak").lastName("Ramakrishnaiah").phone("9999")
            .email("valid@email.com").password(Base64.getEncoder().encodeToString("password".getBytes()))
            .role(User.UserRole.ADMIN).status(User.UserStatus.ACTIVE)
            .build();
        userResourceService.saveUser(userRequest);
      }
    }

  }
}
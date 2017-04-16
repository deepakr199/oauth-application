package injector;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import lombok.extern.slf4j.Slf4j;
import play.libs.Json;


/**
 * Module that configures a Jackson ObjectMapper for JSON (de)serialization.
 * <p>
 * <p>This ObjectMapper can be used via injection or through Play's own {@link Json} class.
 */
@Slf4j
public class JacksonModule extends AbstractModule {

  public static final String USER_RESOURCE_JSON = "USER_RESOURCE_JSON";

  @Override
  protected void configure() {
    bind(ObjectMapper.class)
        .annotatedWith(Names.named(USER_RESOURCE_JSON))
        .toInstance(configureObjectMapper());
  }

  /**
   * Creates an object mapper and configures it according to this services needs.
   *
   * @return object mapper with configurations
   */
  public ObjectMapper configureObjectMapper() {
    ObjectMapper jsonMapper = new ObjectMapper();
    jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    jsonMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
    jsonMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    jsonMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return jsonMapper;
  }


}

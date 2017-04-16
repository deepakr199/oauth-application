package controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;

import dao.CounterDao;
import dao.UserResourceDao;
import helper.Secured;
import injector.MongoModule;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import play.Application;
import play.Configuration;
import play.Environment;
import play.Mode;
import play.api.inject.Binding;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UserResourceTest extends WithApplication {

  private Secured securedMock = mock(Secured.class);

  @Override
  protected Application provideApplication() {
    MockitoAnnotations.initMocks(this);

    return new GuiceApplicationBuilder()
        .in(new Environment(new File("."), getClass().getClassLoader(), Mode.TEST))
        .disable(disableModules())
        .bindings(bindings())
        .configure(new Configuration(getConfig()))
        .build();
  }

  protected Class[] disableModules() {
    return new Class[]{MongoModule.class};
  }

  protected Map<String, Object> getConfig() {
    return new HashMap<>();
  }

  protected Binding<?>[] bindings() {
    return new Binding[]{
        bind(UserResourceDao.class).toInstance(mock(UserResourceDao.class)),
        bind(CounterDao.class).toInstance(mock(CounterDao.class)),
        bind(Secured.class).toInstance(securedMock)
    };
  }

  @Test
  public void getUserSuccessTest() {
    when(securedMock.getUsername(any(Http.Context.class))).thenReturn("true");
    Http.RequestBuilder request = Helpers
        .fakeRequest("GET", "/internal/v1/user");
    Result result = Helpers.route(request);
    Assert.assertEquals(Http.Status.OK, result.status());
  }

}

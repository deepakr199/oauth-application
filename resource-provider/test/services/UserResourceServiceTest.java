package services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dao.CounterDao;
import dao.UserResourceDao;
import models.request.UserRequest;
import models.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class UserResourceServiceTest {

  private UserResourceService userResourceService;

  @Mock
  private UserResourceDao userResourceDaoMock;
  @Mock
  private CounterDao counterDaoMock;

  @Before
  public void setUp() {
    userResourceService = new UserResourceService(userResourceDaoMock, counterDaoMock);
  }

  @Test
  public void saveUserTest() {
    when(userResourceDaoMock.saveUser(any())).thenReturn(1L);
    when(counterDaoMock.getNextId("users")).thenReturn(1L);

    UserRequest userRequest = UserRequest.builder()
        .email("valid@email.com").password("password")
        .firstName("deepak").build();
    Long id = userResourceService.saveUser(userRequest);

    assertEquals(1, id.longValue());
    verify(counterDaoMock).getNextId("users");
    verify(userResourceDaoMock).saveUser(any());
  }

  @Test
  public void getUsersTest() {
    when(userResourceDaoMock.getUsers()).thenReturn(new ArrayList<User>());

    userResourceService.getUsers();

    verify(userResourceDaoMock).getUsers();
  }

}

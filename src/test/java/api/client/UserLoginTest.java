package api.client;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static util.DataGenerator.generateUserDto;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;
import util.Ryuk;

@SuppressWarnings("DuplicateStringLiteralInspection")
public class UserLoginTest {

  private final Ryuk ryuk = new Ryuk();

  @After
  public void afterEach() {
    ryuk.wakeUp();
  }

  @Test
  @DisplayName("Успешный логин существующим пользователем")
  public void shouldLoginByExistingUser() {
    var user = generateUserDto();

    AuthClient.register(ryuk.remember(user))
        .then().assertThat()
        .statusCode(200)
        .body("success", equalTo(true));

    AuthClient.login(user.getEmail(), user.getPassword())
        .then().assertThat()
        .statusCode(200)
        .body("success", equalTo(true))
        .body("message", nullValue())
        .body("user.email", equalTo(user.getEmail()))
        .body("user.name", equalTo(user.getName()))
        .body("accessToken", notNullValue(String.class))
        .body("refreshToken", notNullValue(String.class));
  }

  @Test
  @DisplayName("Логин с некорректным паролем")
  public void shouldFailOnLoginWithIncorrectPassword() {
    var user = generateUserDto();

    AuthClient.register(ryuk.remember(user))
        .then().assertThat()
        .statusCode(200)
        .body("success", equalTo(true));

    AuthClient.login(user.getEmail(), "incorrect")
        .then().assertThat()
        .statusCode(401)
        .body("success", equalTo(false))
        .body("message", equalTo("email or password are incorrect"))
        .body("user.email", nullValue())
        .body("user.name", nullValue())
        .body("accessToken", nullValue())
        .body("refreshToken", nullValue());
  }

  @Test
  @DisplayName("Логин под несуществующим пользователем")
  public void shouldFailOnLoginByNotExistingUser() {
    var user = generateUserDto();

    AuthClient.login(user.getEmail(), user.getPassword())
        .then().assertThat()
        .statusCode(401)
        .body("success", equalTo(false))
        .body("message", equalTo("email or password are incorrect"))
        .body("user.email", nullValue())
        .body("user.name", nullValue())
        .body("accessToken", nullValue())
        .body("refreshToken", nullValue());
  }
}
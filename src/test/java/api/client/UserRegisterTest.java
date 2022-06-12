package api.client;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static util.DataGenerator.generateUserDto;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;

@SuppressWarnings("DuplicateStringLiteralInspection")
public class UserRegisterTest {

  @Test
  @DisplayName("Успешная регистрация нового пользователя")
  public void shouldRegisterUniqueUser() {
    var user = generateUserDto();

    UserClient.register(user)
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
  @DisplayName("Регистрация уже существующего пользователя")
  public void shouldFailOnRegisteringExistingUser() {
    var user = generateUserDto();

    UserClient.register(user)
        .then().assertThat()
        .statusCode(200)
        .body("success", equalTo(true));

    UserClient.register(user)
        .then().assertThat()
        .statusCode(403)
        .body("success", equalTo(false))
        .body("message", equalTo("User already exists"))
        .body("user.email", nullValue())
        .body("user.name", nullValue())
        .body("accessToken", nullValue())
        .body("refreshToken", nullValue());
  }

  @Test
  @DisplayName("Регистрация пользователя без E-mail")
  public void shouldFailOnRegisteringUserWithoutEmail() {
    var user = generateUserDto();
    user.setEmail(null);

    UserClient.register(user)
        .then().assertThat()
        .statusCode(403)
        .body("success", equalTo(false))
        .body("message", equalTo("Email, password and name are required fields"))
        .body("user.email", nullValue())
        .body("user.name", nullValue())
        .body("accessToken", nullValue())
        .body("refreshToken", nullValue());
  }

  @Test
  @DisplayName("Регистрация пользователя без имени")
  public void shouldFailOnRegisteringUserWithoutName() {
    var user = generateUserDto();
    user.setName(null);

    UserClient.register(user)
        .then().assertThat()
        .statusCode(403)
        .body("success", equalTo(false))
        .body("message", equalTo("Email, password and name are required fields"))
        .body("user.email", nullValue())
        .body("user.name", nullValue())
        .body("accessToken", nullValue())
        .body("refreshToken", nullValue());
  }

  @Test
  @DisplayName("Регистрация пользователя без пароля")
  public void shouldFailOnRegisteringUserWithoutPassword() {
    var user = generateUserDto();
    user.setPassword(null);

    UserClient.register(user)
        .then().assertThat()
        .statusCode(403)
        .body("success", equalTo(false))
        .body("message", equalTo("Email, password and name are required fields"))
        .body("user.email", nullValue())
        .body("user.name", nullValue())
        .body("accessToken", nullValue())
        .body("refreshToken", nullValue());
  }
}
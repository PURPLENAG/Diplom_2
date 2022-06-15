package api.client;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static util.DataGenerator.generate36RadixId;
import static util.DataGenerator.generateEmail;
import static util.DataGenerator.generateLogin;
import static util.DataGenerator.generateUserDto;

import api.model.UserDto;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import java.util.Arrays;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.Ryuk;

@SuppressWarnings("DuplicateStringLiteralInspection")
public class UserUpdateTest {

  private final Ryuk ryuk = new Ryuk();

  private UserDto user;
  private String accessToken;

  private enum UserField {
    NAME, EMAIL, PASSWORD
  }

  private enum ExpectedResponse {
    OK, UNAUTHORIZED, EMAIL_ALREADY_USED
  }

  @Before
  public void beforeEach() {
    user = generateUserDto();

    AuthClient.register(ryuk.remember(user))
        .then().assertThat()
        .statusCode(200)
        .body("success", equalTo(true));

    accessToken = AuthClient.login(user.getEmail(), user.getPassword())
        .then().assertThat()
        .statusCode(200)
        .body("success", equalTo(true))
        .body("accessToken", notNullValue(String.class))
        .extract().body().jsonPath().getString("accessToken");
  }

  @After
  public void afterEach() {
    ryuk.wakeUp();
  }

  @Step("Проверка пользователя")
  private void checkUser(String accessToken, UserDto user, UserDto patch,
      ExpectedResponse expectedResponse) {
    var response = AuthClient.getUser(accessToken).then();

    if (expectedResponse == ExpectedResponse.OK) {
      response.assertThat()
          .statusCode(200)
          .body("success", equalTo(true))
          .body("message", nullValue())
          .body("user.email", equalTo(Optional.ofNullable(patch)
              .map(UserDto::getEmail)
              .orElseGet(user::getEmail)))
          .body("user.name", equalTo(Optional.ofNullable(patch)
              .map(UserDto::getName)
              .orElseGet(user::getName)))
          .body("user.password", nullValue());
    } else if (expectedResponse == ExpectedResponse.UNAUTHORIZED) {
      response.assertThat()
          .statusCode(401)
          .body("success", equalTo(false))
          .body("message", equalTo("You should be authorised"))
          .body("user.email", nullValue())
          .body("user.name", nullValue())
          .body("user.password", nullValue());
    } else {
      throw new UnsupportedOperationException();
    }
  }

  @Step("Изменение пользователя")
  private void patchUser(String accessToken, UserDto user, UserDto patch,
      ExpectedResponse expectedResponse) {
    var response = AuthClient.patchUser(accessToken, patch).then();

    if (expectedResponse == ExpectedResponse.OK) {
      response.assertThat()
          .statusCode(200)
          .body("success", equalTo(true))
          .body("user.email", equalTo(Optional.ofNullable(patch)
              .map(UserDto::getEmail)
              .orElseGet(user::getEmail)))
          .body("user.name", equalTo(Optional.ofNullable(patch)
              .map(UserDto::getName)
              .orElseGet(user::getName)))
          .body("user.password", nullValue());
    } else if (expectedResponse == ExpectedResponse.UNAUTHORIZED) {
      response.assertThat()
          .statusCode(401)
          .body("success", equalTo(false))
          .body("message", equalTo("You should be authorised"))
          .body("user.email", nullValue())
          .body("user.name", nullValue())
          .body("user.password", nullValue());
    } else if (expectedResponse == ExpectedResponse.EMAIL_ALREADY_USED) {
      response.assertThat()
          .statusCode(403)
          .body("success", equalTo(false))
          .body("message", equalTo("User with such email already exists"))
          .body("user.email", nullValue())
          .body("user.name", nullValue())
          .body("user.password", nullValue());
    } else {
      throw new UnsupportedOperationException();
    }
  }

  @Step("Логин с верными учётными данными")
  private void loginByExisting(String email, String password) {
    AuthClient.login(email, password)
        .then().assertThat()
        .statusCode(200)
        .body("success", equalTo(true))
        .body("accessToken", notNullValue(String.class))
        .body("user.email", equalTo(user.getEmail()))
        .body("user.name", equalTo(user.getName()))
        .body("user.password", nullValue());
  }

  @Step("Логин с неверными учётными данными")
  private void loginByNotExisting(String email, String password) {
    AuthClient.login(email, password)
        .then().assertThat()
        .statusCode(401)
        .body("success", equalTo(false))
        .body("message", equalTo("email or password are incorrect"))
        .body("user.email", nullValue())
        .body("user.name", nullValue())
        .body("accessToken", nullValue())
        .body("refreshToken", nullValue());
  }

  private UserDto generatePatch(UserField... fields) {
    var fieldsList = Arrays.asList(fields);
    return new UserDto(
        fieldsList.contains(UserField.NAME) ? generateLogin() : null,
        fieldsList.contains(UserField.EMAIL) ? generateEmail() : null,
        fieldsList.contains(UserField.PASSWORD) ? generate36RadixId() : null);
  }

  @Test
  @DisplayName("Получения информации о текущем пользователе")
  public void shouldReturnAuthorizedUser() {
    checkUser(accessToken, user, null, ExpectedResponse.OK);
  }

  @Test
  @DisplayName("Получение информации о текущем пользователе без авторизации")
  public void shouldFailOnGettingAuthorizedUserWithoutAuthorization() {
    checkUser(null, user, null, ExpectedResponse.UNAUTHORIZED);
  }

  @Test
  @DisplayName("Обновление имени текущего пользователя")
  public void shouldUpdateNameForAuthorizedUser() {
    checkUser(accessToken, user, null, ExpectedResponse.OK);
    var patch = generatePatch(UserField.NAME);
    patchUser(accessToken, user, patch, ExpectedResponse.OK);
    checkUser(accessToken, user, patch, ExpectedResponse.OK);
  }

  @Test
  @DisplayName("Обновление имени текущего пользователя без авторизации")
  public void shouldFailOnUpdateNameWithoutAuthorization() {
    checkUser(accessToken, user, null, ExpectedResponse.OK);
    var patch = generatePatch(UserField.NAME);
    patchUser(null, user, patch, ExpectedResponse.UNAUTHORIZED);
    checkUser(accessToken, user, null, ExpectedResponse.OK);
  }

  @Test
  @DisplayName("Обновление E-mail текущего пользователя")
  public void shouldUpdateEmailForAuthorizedUser() {
    checkUser(accessToken, user, null, ExpectedResponse.OK);
    var patch = generatePatch(UserField.EMAIL);
    ryuk.remember(patch.getEmail(), user.getPassword());
    patchUser(accessToken, user, patch, ExpectedResponse.OK);
    checkUser(accessToken, user, patch, ExpectedResponse.OK);
  }

  @Test
  @DisplayName("Обновление E-mail текущего пользователя на уже занятый адрес почты")
  public void shouldFailOnUpdateEmailByAlreadyUsed() {
    var anotherUser = generateUserDto();
    AuthClient.register(ryuk.remember(anotherUser))
        .then().assertThat()
        .statusCode(200)
        .body("success", equalTo(true));

    checkUser(accessToken, user, null, ExpectedResponse.OK);
    var patch = new UserDto();
    patch.setEmail(anotherUser.getEmail());
    ryuk.remember(patch.getEmail(), user.getPassword());
    patchUser(accessToken, user, patch, ExpectedResponse.EMAIL_ALREADY_USED);
    checkUser(accessToken, user, null, ExpectedResponse.OK);
  }

  @Test
  @DisplayName("Обновление E-mail текущего пользователя без авторизации")
  public void shouldFailOnUpdateEmailWithoutAuthorization() {
    checkUser(accessToken, user, null, ExpectedResponse.OK);
    var patch = generatePatch(UserField.EMAIL);
    ryuk.remember(patch.getEmail(), user.getPassword());
    patchUser(null, user, patch, ExpectedResponse.UNAUTHORIZED);
    checkUser(accessToken, user, null, ExpectedResponse.OK);
  }

  @Test
  @DisplayName("Обновление пароля текущего пользователя")
  public void shouldUpdatePasswordForAuthorizedUser() {
    checkUser(accessToken, user, null, ExpectedResponse.OK);
    var patch = generatePatch(UserField.PASSWORD);
    ryuk.remember(user.getEmail(), patch.getPassword());
    patchUser(accessToken, user, patch, ExpectedResponse.OK);
    checkUser(accessToken, user, patch, ExpectedResponse.OK);
    loginByExisting(user.getEmail(), patch.getPassword());
    loginByNotExisting(user.getEmail(), user.getPassword());
  }

  @Test
  @DisplayName("Обновление пароля текущего пользователя без авторизации")
  public void shouldFailOnUpdatePasswordWithoutAuthorization() {
    checkUser(accessToken, user, null, ExpectedResponse.OK);
    var patch = generatePatch(UserField.PASSWORD);
    ryuk.remember(user.getEmail(), patch.getPassword());
    patchUser(null, user, patch, ExpectedResponse.UNAUTHORIZED);
    checkUser(accessToken, user, null, ExpectedResponse.OK);
    loginByExisting(user.getEmail(), user.getPassword());
    loginByNotExisting(user.getEmail(), patch.getPassword());
  }
}

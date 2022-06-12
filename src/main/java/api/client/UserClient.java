package api.client;

import static io.restassured.RestAssured.given;

import api.model.CredentialsDto;
import api.model.UserDto;
import config.StellarApiConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public abstract class UserClient {

  public static Response register(UserDto user) {
    return given()
        .baseUri(StellarApiConfig.BASE_URL)
        .contentType(ContentType.JSON)
        .body(user)
        .post("api/auth/register");
  }

  public static Response login(String email, String password) {
    return login(new CredentialsDto(email, password));
  }

  public static Response login(CredentialsDto credentials) {
    return given()
        .baseUri(StellarApiConfig.BASE_URL)
        .contentType(ContentType.JSON)
        .body(credentials)
        .post("api/auth/login");
  }

  public static Response getUser(String token) {
    var request = given()
        .baseUri(StellarApiConfig.BASE_URL);

    if (token != null) {
      request = request.header("Authorization", token);
    }

    return request.get("api/auth/user");
  }

  public static Response patchUser(String token, UserDto user) {
    var request = given()
        .baseUri(StellarApiConfig.BASE_URL)
        .body(user);

    if (token != null) {
      request = request.header("Authorization", token);
    }

    return request.patch("api/auth/user");
  }

}

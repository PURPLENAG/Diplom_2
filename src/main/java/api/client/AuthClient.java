package api.client;

import api.model.CredentialDto;
import api.model.UserDto;
import config.StellarApiConfig;
import io.restassured.response.Response;

public abstract class AuthClient extends BaseStellarClient {

  public static Response register(UserDto user) {
    return spec().jsonBody(user).post(StellarApiConfig.AUTH_REGISTER_PATH);
  }

  public static Response login(String email, String password) {
    return login(new CredentialDto(email, password));
  }

  public static Response login(CredentialDto credentials) {
    return spec().jsonBody(credentials).post(StellarApiConfig.AUTH_LOGIN_PATH);
  }

  public static Response getUser(String token) {
    return spec().oauth2(token).get(StellarApiConfig.AUTH_USER_PATH);
  }

  public static Response patchUser(String token, UserDto user) {
    return spec().oauth2(token).jsonBody(user).patch(StellarApiConfig.AUTH_USER_PATH);
  }

  public static Response deleteUser(String token) {
    return spec().oauth2(token).delete(StellarApiConfig.AUTH_USER_PATH);
  }

}

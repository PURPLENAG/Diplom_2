package util;

import api.client.AuthClient;
import api.model.CredentialDto;
import api.model.UserDto;

import java.util.ArrayList;
import java.util.List;

public class Ryuk {
  private final List<CredentialDto> credentials = new ArrayList<>();

  public CredentialDto remember(CredentialDto credential) {
    credentials.add(credential);
    return credential;
  }

  public UserDto remember(UserDto user) {
    credentials.add(new CredentialDto(user.getEmail(), user.getPassword()));
    return user;
  }

  public void remember(String email, String password) {
    credentials.add(new CredentialDto(email, password));
  }

  public void wakeUp() {
    for (var credential : credentials) {
      var loginResponse = AuthClient.login(credential);
      if (loginResponse.statusCode() != 200) {
        continue;
      }
      var accessToken = loginResponse.body().jsonPath().getString("accessToken");
      AuthClient.deleteUser(accessToken);
    }
  }
}

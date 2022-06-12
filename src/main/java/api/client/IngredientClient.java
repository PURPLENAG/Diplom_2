package api.client;

import static io.restassured.RestAssured.given;

import config.StellarApiConfig;
import io.restassured.response.Response;

public abstract class IngredientClient {

  public static Response getAll() {
    return given()
        .baseUri(StellarApiConfig.BASE_URL)
        .get("api/ingredients");
  }

}

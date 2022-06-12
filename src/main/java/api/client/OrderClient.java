package api.client;

import static io.restassured.RestAssured.given;

import api.model.OrderDto;
import config.StellarApiConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public abstract class OrderClient {

  public static Response create(String token, OrderDto order) {
    var request = given()
        .baseUri(StellarApiConfig.BASE_URL)
        .contentType(ContentType.JSON)
        .body(order);

    if (token != null) {
      request = request.header("Authorization", token);
    }

    return request.post("api/orders");
  }

  public static Response getAllForUser(String token) {
    var request = given()
        .baseUri(StellarApiConfig.BASE_URL);

    if (token != null) {
      request = request.header("Authorization", token);
    }

    return request.get("api/orders");
  }

}

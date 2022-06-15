package api.client;

import api.model.OrderDto;
import config.StellarApiConfig;
import io.restassured.response.Response;

public abstract class OrderClient extends BaseStellarClient {

  public static Response create(String token, OrderDto order) {
    return spec().oauth2(token).jsonBody(order).post(StellarApiConfig.ORDERS_PATH);
  }

  public static Response getAllForUser(String token) {
    return spec().oauth2(token).get(StellarApiConfig.ORDERS_PATH);
  }

}

package api.client;

import config.StellarApiConfig;
import io.restassured.response.Response;

public abstract class IngredientClient extends BaseStellarClient {

  public static Response getAll() {
    return spec().get(StellarApiConfig.INGREDIENTS_PATH);
  }

}

package api.client;

import config.StellarApiConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public abstract class BaseStellarClient {

  private static final RequestSpecification baseRequestSpec = given()
      .baseUri(StellarApiConfig.BASE_URL);

  protected static RequestSpecificationBuilder spec() {
    return new RequestSpecificationBuilder();
  }

  public static class RequestSpecificationBuilder {
    private RequestSpecification spec = given().spec(baseRequestSpec);

    public RequestSpecificationBuilder oauth2(String accessToken) {
      if (accessToken != null) {
        spec = spec.header("Authorization", accessToken);
      }
      return this;
    }

    public RequestSpecificationBuilder jsonBody(Object obj) {
      if (obj != null) {
        spec = spec.contentType(ContentType.JSON).body(obj);
      }
      return this;
    }

    public RequestSpecification build() {
      return spec;
    }

    public Response get(String path) {
      return build().get(path);
    }

    public Response post(String path) {
      return build().post(path);
    }

    public Response patch(String path) {
      return build().patch(path);
    }

    public Response delete(String path) {
      return build().delete(path);
    }
  }

}

package api.client;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static util.DataGenerator.generateUserDto;

import api.model.GetAllIngredientsResponseDto;
import api.model.IngredientDto;
import api.model.OrderDto;
import api.model.UserDto;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.Ryuk;

@SuppressWarnings("DuplicateStringLiteralInspection")
public class OrderCreateTest {

  private final Ryuk ryuk = new Ryuk();

  private static final int MIN_INGREDIENTS_COUNT = 3;
  private static final String INVALID_INGREDIENT_ID = "65c1c5a77d1a82001bdaba9b";

  private static List<IngredientDto> ingredients;
  private static IngredientDto ingredient1;
  private static IngredientDto ingredient2;
  private static IngredientDto ingredient3;

  private UserDto user;
  private String accessToken;

  private enum ExpectedResponse {
    OK, UNAUTHORIZED, NO_INGREDIENTS, INVALID_INGREDIENT
  }

  @BeforeClass
  public static void beforeClass() {
    var ingredientsResponse = IngredientClient.getAll()
        .then().assertThat()
        .statusCode(200)
        .extract().as(GetAllIngredientsResponseDto.class);

    assertNotNull(ingredientsResponse);
    assertTrue(ingredientsResponse.getSuccess());
    assertNotNull(ingredientsResponse.getData());
    assertTrue(ingredientsResponse.getData().size() >= MIN_INGREDIENTS_COUNT);
    ingredientsResponse.getData().forEach(it -> assertNotNull(it.getId()));

    ingredients = ingredientsResponse.getData();
    ingredient1 = ingredients.get(0);
    ingredient2 = ingredients.get(1);
    ingredient3 = ingredients.get(2);
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

  @Step("Создание заказа")
  private void createOrder(String accessToken, OrderDto order, ExpectedResponse expectedResponse) {
    var response = OrderClient.create(accessToken, order).then();

    if (expectedResponse == ExpectedResponse.OK) {
      response.assertThat()
          .statusCode(200)
          .body("success", equalTo(true))
          .body("message", nullValue())
          .body("name", notNullValue(String.class))
          .body("order.number", notNullValue(Number.class));
    } else if (expectedResponse == ExpectedResponse.UNAUTHORIZED) {
      response.assertThat()
          .statusCode(401)
          .body("success", equalTo(false))
          .body("message", equalTo("You should be authorised"))
          .body("name", nullValue())
          .body("order.number", nullValue());
    } else if (expectedResponse == ExpectedResponse.NO_INGREDIENTS) {
      response.assertThat()
          .statusCode(400)
          .body("success", equalTo(false))
          .body("message", equalTo("Ingredient ids must be provided"))
          .body("name", nullValue())
          .body("order.number", nullValue());
    } else if (expectedResponse == ExpectedResponse.INVALID_INGREDIENT) {
      response.assertThat()
          .statusCode(500)
          .body("success", nullValue())
          .body("message", nullValue())
          .body("name", nullValue())
          .body("order.number", nullValue());
    } else {
      throw new UnsupportedOperationException();
    }
  }

  @Test
  @DisplayName("Создание заказа с авторизацией")
  public void shouldCreateOrderIfAuthorized() {
    var order = new OrderDto(List.of(ingredient1.getId(), ingredient2.getId()));
    createOrder(accessToken, order, ExpectedResponse.OK);
  }

  @Test
  @DisplayName("Создание заказа без авторизации")
  public void shouldFailOnCreateOrderIfNotAuthorized() {
    var order = new OrderDto(List.of(ingredient1.getId(), ingredient2.getId()));
    createOrder(null, order, ExpectedResponse.UNAUTHORIZED);
  }

  @Test
  @DisplayName("Создание заказа без ингредиентов")
  public void shouldFailOnCreateOrderWithoutIngredientsIfAuthorized() {
    var order = new OrderDto(List.of());
    createOrder(accessToken, order, ExpectedResponse.NO_INGREDIENTS);
  }

  @Test
  @DisplayName("Создание заказа с некорректным ингредиентом")
  public void shouldFailOnCreateOrderWithOnlyInvalidIngredientIfAuthorized() {
    var order = new OrderDto(List.of(INVALID_INGREDIENT_ID));
    createOrder(accessToken, order, ExpectedResponse.INVALID_INGREDIENT);
  }

  @Test
  @DisplayName("Создание заказа с корректным и некорректным ингредиентом")
  public void shouldFailOnCreateOrderWithValidAndInvalidIngredientIfAuthorized() {
    var order = new OrderDto(List.of(ingredient1.getId(), INVALID_INGREDIENT_ID));
    createOrder(accessToken, order, ExpectedResponse.INVALID_INGREDIENT);
  }
}
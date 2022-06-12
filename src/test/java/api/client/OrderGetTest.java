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
import io.qameta.allure.junit4.DisplayName;
import java.util.List;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("DuplicateStringLiteralInspection")
public class OrderGetTest {

  private static final int MIN_INGREDIENTS_COUNT = 3;
  private static final String INVALID_INGREDIENT_ID = "65c1c5a77d1a82001bdaba9b";

  private static List<IngredientDto> ingredients;
  private static IngredientDto ingredient1;
  private static IngredientDto ingredient2;
  private static IngredientDto ingredient3;

  private UserDto user;
  private OrderDto order1;
  private OrderDto order2;
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

    UserClient.register(user)
        .then().assertThat()
        .statusCode(200)
        .body("success", equalTo(true));

    accessToken = UserClient.login(user.getEmail(), user.getPassword())
        .then().assertThat()
        .statusCode(200)
        .body("success", equalTo(true))
        .body("accessToken", notNullValue(String.class))
        .extract().body().jsonPath().getString("accessToken");

    order1 = new OrderDto(List.of(ingredient1.getId(), ingredient2.getId()));
    order2 = new OrderDto(List.of(ingredient3.getId()));

    OrderClient.create(accessToken, order1)
        .then().assertThat()
        .statusCode(200)
        .body("success", equalTo(true));

    OrderClient.create(accessToken, order2)
        .then().assertThat()
        .statusCode(200)
        .body("success", equalTo(true));
  }

  @Test
  @DisplayName("Получение всех заказов пользователя")
  public void shouldGetUserOrdersIfAuthorized() {
    OrderClient.getAllForUser(accessToken)
        .then().assertThat()
        .statusCode(200)
        .body("success", equalTo(true))
        .body("total", equalTo(2))
        .body("totalToday", equalTo(2))
        .body("orders.size()", equalTo(2));
  }

  @Test
  @DisplayName("Получение всех заказов пользователя без авторизации")
  public void shouldFailOnGetUserOrdersIfNotAuthorized() {
    OrderClient.getAllForUser(null)
        .then().assertThat()
        .statusCode(401)
        .body("success", equalTo(false))
        .body("message", equalTo("You should be authorised"))
        .body("total", nullValue())
        .body("totalToday", nullValue())
        .body("orders", nullValue());
  }
}
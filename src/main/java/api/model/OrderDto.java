package api.model;

import java.util.List;

public class OrderDto {

  private List<String> ingredients;

  public OrderDto() {
  }

  public OrderDto(List<String> ingredients) {
    this.ingredients = ingredients;
  }

  public List<String> getIngredients() {
    return ingredients;
  }

  public void setIngredients(List<String> ingredients) {
    this.ingredients = ingredients;
  }
}

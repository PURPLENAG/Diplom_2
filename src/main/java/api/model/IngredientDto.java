package api.model;

import com.google.gson.annotations.SerializedName;

public class IngredientDto {

  @SerializedName("_id")
  private String id;
  private String name;
  private Double price;
  private String type;

  public IngredientDto() {
  }

  public IngredientDto(String id, String name, Double price, String type) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}

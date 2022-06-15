package api.model;

import java.util.List;

public class GetAllIngredientsResponseDto {

  private Boolean success;

  private List<IngredientDto> data;

  public GetAllIngredientsResponseDto() {
  }

  public GetAllIngredientsResponseDto(Boolean success, List<IngredientDto> data) {
    this.success = success;
    this.data = data;
  }

  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public List<IngredientDto> getData() {
    return data;
  }

  public void setData(List<IngredientDto> data) {
    this.data = data;
  }
}

package hu.soft4d.yokudlela3.controller.dish.model.response;

import hu.soft4d.yokudlela3.business.model.dish.model.Dish;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Schema(description = "List of dish")
@Value
@SuperBuilder
@Jacksonized
public class DishListResponse {

    @Schema(description = "List of dishes")
    List<Dish> contents;

}

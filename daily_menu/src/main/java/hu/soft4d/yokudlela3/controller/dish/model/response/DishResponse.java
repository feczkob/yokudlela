package hu.soft4d.yokudlela3.controller.dish.model.response;

import hu.soft4d.yokudlela3.business.model.dish.model.Dish;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Schema(description = "Dish")
@Value
@SuperBuilder
@Jacksonized
public class DishResponse {

    @Schema(description = "Dish")
    Dish content;

}

package com.yokudlela.dailymenu.controller.dish.model.response;

import com.yokudlela.dailymenu.business.model.dish.model.Dish;

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

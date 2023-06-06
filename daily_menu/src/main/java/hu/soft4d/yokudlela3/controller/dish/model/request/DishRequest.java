package hu.soft4d.yokudlela3.controller.dish.model.request;

import hu.soft4d.yokudlela3.client.persistence.entity.enumeration.Category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.openapitools.jackson.nullable.JsonNullable;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Schema(description = "Dish")
@Value
@Builder
@Jacksonized
@EqualsAndHashCode
public class DishRequest {

    @Schema(description = "Name of given dish", example = "Mushroom soup, etc.", type = "String", required = true)
    @NotBlank(message = "error.dish.name.required")
    String name;

    @Schema(description = "Recipe of given dish, which describes ingredients and cooking process",
            example = "NOTE: currently just an ID, later can be generated type (Recipe) from Recipe API",
            type = "integer", required = true)
    @NotNull(message = "error.dish.recipe.required")
    Integer recipe; // currently, just an ID, later can be generated type (Recipe) from Recipe API

    @Schema(description = "Category of given dish", example = "soup, main, dessert, etc.", required = true)
    @NotNull(message = "error.dish.category.required")
    @Enumerated(EnumType.STRING)
    Category category;

    @Schema(description = "Nominal price of given dish (it can be changed depends on various circumstances)",
            example = "100, 200, etc., and sorry for dummy example",
            type = "integer", required = true)
    @NotNull(message = "error.dish.price.required")
    @Positive(message = "error.dish.price.positive")
    @Min(value = 100, message = "error.dish.price.min")
    @Max(value = 5000, message = "error.dish.price.max")
    Integer price;

    @Schema(description = "Dish is active or not", example = "true/false")
    JsonNullable<Boolean> active;

}

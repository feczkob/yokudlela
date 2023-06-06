package com.yokudlela.dailymenu.controller.menuitem.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Schema(description = "Menu item amount request")
@Value
@Builder
@Jacksonized
@EqualsAndHashCode
public class MenuItemAmountRelatedRequest {

    @Schema(description = "Amount of given daily menu item",
            example = "500, 1000, etc., and sorry for dummy example",
            type = "integer", required = true)
    @NotNull(message = "error.item.amount.required")
    @Positive(message = "error.item.amount.positive")
    @Min(value = 0, message = "error.item.amount.min")
    @Max(value = 5000, message = "error.item.amount.max")
    Integer amount;

}

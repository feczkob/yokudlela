package hu.soft4d.yokudlela3.controller.menuitem.model.request;

import hu.soft4d.yokudlela3.client.persistence.entity.enumeration.Section;
import hu.soft4d.yokudlela3.client.persistence.entity.enumeration.Variant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.annotations.Type;

import java.util.UUID;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Schema(description = "Menu item as a part of a menu on given day")
@Value
@Builder
@Jacksonized
@EqualsAndHashCode
public class MenuItemRequest {

    @Schema(description = "Section or group of daily menu item", example = "Normal, Vega, Child, etc.", required = true)
    @NotNull(message = "error.item.section.required")
    @Enumerated(EnumType.STRING)
    Section section;

    @Schema(description = "Variant of daily menu item - only one letter is used generally", example = "A, B, C or ever you want", required = true)
    @NotNull(message = "error.item.variant.required")
    @Enumerated(EnumType.STRING)
    Variant variant;

    @Schema(description = "ID of daily menu", example = "UUID", required = true, type = "uuid")
    @NotNull(message = "error.item.menu.required")
    @Type(type = "uuid-char")
    UUID menuId;

    @Schema(description = "ID of dish we provide as a part of daily menu item", example = "UUID of the dish JSON object", required = true, type = "uuid")
    @NotNull(message = "error.item.dish.required")
    @Type(type = "uuid-char")
    UUID dishId;

    @Schema(description = "Price of daily menu item (it can be changed depends on various circumstances)",
            example = "500, 1000, etc., and sorry for dummy example",
            type = "integer", required = true)
    @NotNull(message = "error.item.price.required")
    @Positive(message = "error.item.price.positive")
    @Min(value = 100, message = "error.item.price.min")
    @Max(value = 5000, message = "error.item.price.max")
    Integer price;

    @Schema(description = "Amount of given daily menu item currently available",
            example = "500, 1000, etc., and sorry for dummy example",
            type = "integer", required = true)
    @NotNull(message = "error.item.currentAmount.required")
    @PositiveOrZero(message = "error.item.currentAmount.positive")
    @Min(value = 0, message = "error.item.currentAmount.min")
    @Max(value = 5000, message = "error.item.currentAmount.max")
    Integer currentAmount;

    @Schema(description = "Amount of given daily menu item claimed to be done",
            example = "500, 1000, etc., and sorry for dummy example",
            type = "integer", required = true)
    @NotNull(message = "error.item.claimedAmount.required")
    @PositiveOrZero(message = "error.item.claimedAmount.positive")
    @Min(value = 0, message = "error.item.claimedAmount.min")
    @Max(value = 1000, message = "error.item.claimedAmount.max")
    Integer claimedAmount;

    @Schema(description = "Amount of given daily menu item has been already purchased",
            example = "500, 1000, etc., and sorry for dummy example",
            type = "integer", required = true)
    @NotNull(message = "error.item.paidAmount.required")
    @PositiveOrZero(message = "error.item.paidAmount.positive")
    @Min(value = 0, message = "error.item.paidAmount.min")
    @Max(value = 1000, message = "error.item.paidAmount.max")
    Integer paidAmount;

}

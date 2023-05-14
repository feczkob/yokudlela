package com.yokudlela.kitchen.controller.model.request

import io.swagger.v3.oas.annotations.media.Schema
import org.openapitools.jackson.nullable.JsonNullable

@Schema(name = "OrderItemPatch")
data class OrderItemPatch(
    @Schema(description = "Id of the order")
    var orderId: JsonNullable<Int>? = null,
    @Schema(description = "Id of menu item", nullable = false)
    var menuItemId: JsonNullable<Int>? = null,
    @Schema(description = "Quantity", nullable = false)
    var quantity: JsonNullable<Int>? = null,
    @Schema(description = "Details")
    var details: JsonNullable<String>? = null,
)
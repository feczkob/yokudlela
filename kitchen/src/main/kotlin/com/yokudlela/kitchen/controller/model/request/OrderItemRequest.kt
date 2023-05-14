package com.yokudlela.kitchen.controller.model.request

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotNull

@Schema(name = "OrderItemRequest")
open class OrderItemRequest(
    @Schema(description = "Id of the order")
    open var orderId: Int? = null,
    @Schema(description = "Id of menu item", nullable = false)
    @field:NotNull
    open var menuItemId: Int? = 0,
    @Schema(description = "Quantity", nullable = false)
    @field:NotNull
    open var quantity: Int = 0,
    @Schema(description = "Details")
    open var details: String? = null,
)

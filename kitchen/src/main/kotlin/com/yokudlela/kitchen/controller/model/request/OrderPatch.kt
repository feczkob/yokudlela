package com.yokudlela.kitchen.controller.model.request

import io.swagger.v3.oas.annotations.media.Schema
import org.openapitools.jackson.nullable.JsonNullable

@Schema(name = "OrderPatch")
data class OrderPatch(
    @Schema(description = "List of order items", nullable = false)
    var orderItems: JsonNullable<List<OrderItemRequest>>? = null,
    @Schema(description = "Id of the location", nullable = false)
    var locationId: JsonNullable<Int>? = null,
    @Schema(description = "Details")
    var details: JsonNullable<String>? = null
)
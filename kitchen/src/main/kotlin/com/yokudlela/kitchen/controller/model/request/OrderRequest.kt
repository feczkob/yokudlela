package com.yokudlela.kitchen.controller.model.request

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotNull

@Schema(name = "OrderRequest")
data class OrderRequest(
    @Schema(description = "List of order items", nullable = false)
    @field:NotNull
    var orderItems: List<OrderItemRequest>? = null,
    @Schema(description = "Id of the location", nullable = false)
    @field:NotNull
    var locationId: Int? = null,
    @Schema(description = "Details")
    var details: String? = null,
)
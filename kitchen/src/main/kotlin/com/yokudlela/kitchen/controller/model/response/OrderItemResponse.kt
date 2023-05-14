package com.yokudlela.kitchen.controller.model.response

import com.yokudlela.kitchen.business.model.Status
import com.yokudlela.kitchen.controller.Constants
import com.yokudlela.kitchen.controller.model.request.OrderItemRequest
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import javax.ws.rs.Path

@Path(Constants.ORDER_ITEM)
@Schema(name = "OrderItemResponse")
data class OrderItemResponse @JvmOverloads constructor(
    @Schema(description = "Id of the order item", nullable = false)
    var id: Int = 0,
    @Schema(description = "Id of the order")
    override var orderId: Int? = null,
    @Schema(description = "Id of menu item", nullable = false)
    override var menuItemId: Int? = 0,
    @Schema(description = "Quantity", nullable = false)
    override var quantity: Int = 0,
    @Schema(description = "Details")
    override var details: String? = null,
    @Schema(description = "Time of record", nullable = false)
    var timeOfRecord: LocalDateTime = LocalDateTime.now(),
    @Schema(description = "Time of modification", nullable = false)
    var timeOfModification: LocalDateTime? = null,
    @Schema(description = "Time of fulfillment")
    var timeOfFulfillment: LocalDateTime? = null,
    @Schema(description = "Status of the order item", nullable = false)
    var status: Status = Status.OPEN,
) : OrderItemRequest(orderId, menuItemId, quantity, details)

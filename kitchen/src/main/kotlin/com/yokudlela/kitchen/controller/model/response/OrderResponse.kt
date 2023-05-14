package com.yokudlela.kitchen.controller.model.response

import com.yokudlela.kitchen.business.model.Status
import com.yokudlela.kitchen.controller.Constants
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import javax.ws.rs.Path

@Path(Constants.ORDER)
@Schema(name = "OrderResponse")
data class OrderResponse @JvmOverloads constructor(
    @Schema(description = "Id of order", nullable = false)
    var id: Int = 0,
    @Schema(description = "List of order items", nullable = false)
    var orderItems: MutableList<OrderItemResponse> = mutableListOf(),
    @Schema(description = "Time of record", nullable = false)
    var timeOfRecord: LocalDateTime = LocalDateTime.now(),
    @Schema(description = "Time of modification", nullable = false)
    var timeOfModification: LocalDateTime? = null,
    @Schema(description = "Time of fulfillment")
    var timeOfFulfillment: LocalDateTime? = null,
    @Schema(
        description = "PARTIALLY_FINISHED is assigned if any of the order items can not be carried out",
        nullable = false
    )
    var status: Status = Status.OPEN,
    @Schema(description = "Id of the location", nullable = false)
    var locationId: Int = 0,
    @Schema(description = "Details")
    var details: String? = null,
)

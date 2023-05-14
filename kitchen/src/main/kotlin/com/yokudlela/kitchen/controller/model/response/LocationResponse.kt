package com.yokudlela.kitchen.controller.model.response

import com.yokudlela.kitchen.controller.Constants
import io.swagger.v3.oas.annotations.media.Schema
import javax.ws.rs.Path

@Path(Constants.LOCATION)
@Schema(name = "LocationResponse")
data class LocationResponse @JvmOverloads constructor(
    @Schema(description = "Id of the location", nullable = false)
    var id: Int = 0,
    @Schema(description = "Name of the location", nullable = false)
    var name: String = "",
    @Schema(description = "Details")
    var details: String? = null,
    @Schema(description = "Ids of orders associated with this location", nullable = false)
    var orderIds: MutableList<Int> = mutableListOf(),
)

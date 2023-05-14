package com.yokudlela.kitchen.controller.model.request

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank


@Schema(name = "LocationRequest")
data class LocationRequest(
    @Schema(description = "Name of the location", nullable = false)
    @field:NotBlank
    var name: String = "",
    @Schema(description = "Details")
    var details: String? = null,
)

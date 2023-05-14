package com.yokudlela.kitchen.controller.model.request

import io.swagger.v3.oas.annotations.media.Schema
import org.openapitools.jackson.nullable.JsonNullable

@Schema(name = "LocationPatch")
data class LocationPatch(
    @Schema(description = "Name of the location", nullable = false)
    var name: JsonNullable<String>? = null,
    @Schema(description = "Details")
    var details: JsonNullable<String>? = null,
)
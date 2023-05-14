package com.yokudlela.kitchen.controller.model.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "ResponseEntityId")
data class ResponseEntityId(
    @Schema(description = "Id", nullable = false)
    val id: Int,
)

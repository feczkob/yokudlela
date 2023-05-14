package com.yokudlela.kitchen.controller.rest

import com.yokudlela.kitchen.application.AspectLogger
import com.yokudlela.kitchen.controller.Constants.Companion.TEST
import com.yokudlela.kitchen.controller.model.response.ResponseEntityId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.ws.rs.Consumes
import javax.ws.rs.Produces

@Controller
@RequestMapping(TEST)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@AspectLogger
class TestController {

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ResponseEntityId::class)
            )]
        )]
    )
    @Operation(description = "Test")
    @GetMapping
    fun test(): ResponseEntity<ResponseEntityId> {
        return ResponseEntity.ok().body(ResponseEntityId(10))
    }
}
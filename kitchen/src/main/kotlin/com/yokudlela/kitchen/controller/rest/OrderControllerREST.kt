package com.yokudlela.kitchen.controller.rest

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.yokudlela.kitchen.application.AspectLogger
import com.yokudlela.kitchen.application.GenericConverter
import com.yokudlela.kitchen.business.config.OrderConfig
import com.yokudlela.kitchen.business.model.Status
import com.yokudlela.kitchen.controller.Constants.Companion.ALL
import com.yokudlela.kitchen.controller.Constants.Companion.MODIFY
import com.yokudlela.kitchen.controller.Constants.Companion.ORDER
import com.yokudlela.kitchen.controller.Constants.Companion.ORDER_ID
import com.yokudlela.kitchen.controller.Constants.Companion.STATUS
import com.yokudlela.kitchen.controller.converter.OrderToBusinessConverter
import com.yokudlela.kitchen.controller.error.RestApiError
import com.yokudlela.kitchen.controller.model.request.OrderPatch
import com.yokudlela.kitchen.controller.model.request.OrderRequest
import com.yokudlela.kitchen.controller.model.response.OrderResponse
import com.yokudlela.kitchen.controller.validator.PatchedRequestValidator
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.modelmapper.ModelMapper
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.Consumes
import javax.ws.rs.Produces
import javax.ws.rs.core.UriBuilder

@Controller
@Validated
@RequestMapping(ORDER)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@AspectLogger
class OrderControllerREST(
    private val orderConfig: OrderConfig,
    private val modelMapper: ModelMapper,
    private val orderToBusinessConverter: OrderToBusinessConverter,
    private val objectMapper: ObjectMapper,
    private val patchedRequestValidator: PatchedRequestValidator,
    private val converter: GenericConverter
) {

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "201",
            description = "Created",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = OrderResponse::class)
            )]
        ), ApiResponse(
            responseCode = "302",
            description = "Found"
        ), ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = RestApiError::class)
            )]
        ), ApiResponse(
            responseCode = "403",
            description = "Forbidden"
        ), ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = RestApiError::class)
            )]
        )]
    )
    @Operation(
        description = "Create an order",
        security = [SecurityRequirement(name = "openid")]
    )
    @PostMapping
    fun registerOrder(
        @io.swagger.v3.oas.annotations.parameters.RequestBody
        @Valid @NotNull
        @RequestBody order: OrderRequest,
    ): ResponseEntity<Any> {
        // * https://httpwg.org/specs/rfc9110.html#POST
        // * waiter
        val ord = orderToBusinessConverter.convert(order)
            .addOrder()
        val processedUri = UriBuilder.fromResource(OrderResponse::class.java)
            .path(ORDER_ID)
            .build(ord.id)
        return ResponseEntity.created(processedUri)
            .body(modelMapper.map(ord, OrderResponse::class.java))
    }

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = OrderResponse::class)
            )]
        ), ApiResponse(
            responseCode = "302",
            description = "Found"
        ), ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = RestApiError::class)
            )]
        ), ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = RestApiError::class)
            )]
        ), ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = RestApiError::class)
            )]
        )]
    )
    @Operation(
        description = "Get an order",
        security = [SecurityRequirement(name = "openid")]
    )
    @GetMapping(ORDER_ID)
    fun getOrder(
        @Schema(description = "Id of the order to be retrieved", implementation = Int::class)
        @NotNull
        @PathVariable orderId: Int,
    ): ResponseEntity<OrderResponse> {
        // * https://httpwg.org/specs/rfc9110.html#GET

        val business = orderConfig.getOrder()

        return ResponseEntity.ok().body(
            modelMapper.map(business.loadOrder(orderId), OrderResponse::class.java)
        )
    }

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = OrderResponse::class)
            )]
        ), ApiResponse(
            responseCode = "302",
            description = "Found"
        ), ApiResponse(
            responseCode = "201",
            description = "Created",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = OrderResponse::class)
            )]
        ), ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = RestApiError::class)
            )]
        ), ApiResponse(
            responseCode = "403",
            description = "Forbidden"
        ), ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = RestApiError::class)
            )]
        )]
    )
    @Operation(
        description = "Change an existing - or add a new order",
        security = [SecurityRequirement(name = "openid")]
    )
    @PutMapping(MODIFY + ORDER_ID)
    fun modifyOrAddOrder(
        @Schema(description = "Id of the order to be modified", implementation = Int::class)
        @NotNull
        @PathVariable orderId: Int,
        @io.swagger.v3.oas.annotations.parameters.RequestBody
        @Valid @NotNull
        @RequestBody
        order: OrderRequest,
    ): ResponseEntity<OrderResponse> {
        // * counter
        val loadedOrder = modelMapper.map(
            orderToBusinessConverter.convert(order)
                .changeOrAddOrder(orderId), OrderResponse::class.java
        )
        val processedUri = UriBuilder.fromResource(OrderResponse::class.java)
            .path(ORDER_ID)
            .build(loadedOrder.id)
        val httpHeaders = HttpHeaders()
        httpHeaders.location = processedUri
        return if (loadedOrder.id != orderId) {
            ResponseEntity
                .created(processedUri)
                .body(loadedOrder)
        } else {
            ResponseEntity
                .ok()
                .headers(httpHeaders)
                .body(loadedOrder)
        }
    }

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = OrderResponse::class)
            )]
        ), ApiResponse(
            responseCode = "302",
            description = "Found"
        ), ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = RestApiError::class)
            )]
        ), ApiResponse(
            responseCode = "403",
            description = "Forbidden"
        ), ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = RestApiError::class)
            )]
        )]
    )
    @Operation(
        description = "Change an existing order",
        security = [SecurityRequirement(name = "openid")]
    )
    @PatchMapping(ORDER_ID)
    fun modifyOrder(
        @Schema(description = "Id of the order to be modified", implementation = Int::class)
        @NotNull
        @PathVariable
        orderId: Int,
        @io.swagger.v3.oas.annotations.parameters.RequestBody
        @NotNull
        @RequestBody
        orderRequest: OrderPatch,
    ): ResponseEntity<OrderResponse> {
        var businessExisting = orderConfig.getOrder()
        businessExisting = businessExisting.loadOrder(orderId)
        val restExisting = converter.convert(businessExisting, OrderRequest::class.java)

        try {
            objectMapper.updateValue(restExisting, orderRequest)
        } catch (exc: JsonMappingException) {
            throw exc
        }

        patchedRequestValidator.validate(
            restExisting,
            "order",
            "modifyOrder",
            javaClass,
            Int::class.java,
            OrderRequest::class.java
        )

        val business = orderConfig.getOrder()
        converter.convert(restExisting, business)
        business.id = orderId
        business.modify(businessExisting)

        val processedUri = UriBuilder.fromResource(OrderResponse::class.java)
            .path(ORDER_ID)
            .build(orderId)
        val httpHeaders = HttpHeaders()
        httpHeaders.location = processedUri
        val resp = modelMapper.map(business, OrderResponse::class.java)

        return ResponseEntity.ok()
            .headers(httpHeaders).body(resp)
    }

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "204",
            description = "No Content",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = Any::class)
            )]
        ), ApiResponse(
            responseCode = "302",
            description = "Found"
        ), ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = RestApiError::class)
            )]
        ), ApiResponse(
            responseCode = "403",
            description = "Forbidden"
        ), ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = RestApiError::class)
            )]
        ), ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = RestApiError::class)
            )]
        )]
    )
    @Operation(
        description = "Delete an order",
        security = [SecurityRequirement(name = "openid")]
    )
    @DeleteMapping(ORDER_ID)
    fun deleteOrder(
        @Schema(description = "Id of the order to be deleted", implementation = Int::class)
        @NotNull
        @PathVariable orderId: Int,
    ): ResponseEntity<Any> {
        // * https://httpwg.org/specs/rfc9110.html#DELETE
        // * counter, waiter
        orderConfig.getOrder().deleteOrder(orderId)
        return ResponseEntity.noContent().build()
    }

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = OrderResponse::class)
            )]
        ), ApiResponse(
            responseCode = "302",
            description = "Found"
        ), ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = RestApiError::class)
            )]
        ), ApiResponse(
            responseCode = "403",
            description = "Forbidden"
        ), ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = RestApiError::class)
            )]
        )]
    )
    @Operation(
        description = "Change an order's status",
        security = [SecurityRequirement(name = "openid")]
    )
    @PutMapping(STATUS + ORDER_ID)
    @Deprecated(message = "Not implemented correctly yet")
    fun modifyOrderStatus(
        @Schema(description = "Id of the order whose status is to be modified", implementation = Int::class)
        @NotNull
        @PathVariable orderId: Int,
        @io.swagger.v3.oas.annotations.parameters.RequestBody
        @Valid @NotNull
        @RequestBody status: Status,
    ): ResponseEntity<OrderResponse> {
        // * chef, counter
        // TODO check order registration place, set timeOfUpdate
        // * https://httpwg.org/specs/rfc9110.html#PUT
        val business = orderConfig.getOrder()
        val loadedOrder = modelMapper.map(
            business.modifyOrderStatus(orderId, status),
            OrderResponse::class.java
        )
        val processedUri = UriBuilder.fromResource(OrderResponse::class.java)
            .path(ORDER_ID)
            .build(loadedOrder.id)
        val httpHeaders = HttpHeaders()
        httpHeaders.location = processedUri
        return ResponseEntity.ok()
            .headers(httpHeaders)
            .body(loadedOrder)
    }

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = ArraySchema(schema = Schema(implementation = OrderResponse::class))
            )]
        ), ApiResponse(
            responseCode = "302",
            description = "Found"
        ), ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = RestApiError::class)
            )]
        ), ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = RestApiError::class)
            )]
        )]
    )
    @Operation(
        description = "Get all orders",
        security = [SecurityRequirement(name = "openid")]
    )
    @GetMapping(ALL)
    @ResponseBody
    fun getAllOrders(
        @Schema(description = "Status to be filtered for", implementation = Status::class)
        @RequestParam
        status: Status?,
        @Schema(description = "Number of page to be retrieved", implementation = Int::class, required = false)
        @RequestParam(defaultValue = "0", required = false)
        pageNumber: Int,
        @Schema(description = "Size of page to be retrieved", implementation = Int::class, required = false)
        @RequestParam(defaultValue = "30", required = false)
        pageSize: Int,
    ): List<OrderResponse> {
        // * https://httpwg.org/specs/rfc9110.html#GET
        // * chef, counter, waiter
        return orderConfig.getOrder().loadOrders(status, pageNumber, pageSize)
            .map { o -> modelMapper.map(o, OrderResponse::class.java) }
    }

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = ArraySchema(schema = Schema(implementation = Status::class))
            )]
        ), ApiResponse(
            responseCode = "302",
            description = "Found"
        ), ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = RestApiError::class)
            )]
        ), ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = RestApiError::class)
            )]
        )]
    )
    @Operation(
        description = "Get all status values",
        security = [SecurityRequirement(name = "openid")]
    )
    @GetMapping(STATUS)
    @ResponseBody
    fun getStatusValues(): List<Status> {
        return orderConfig.getOrder().loadStatusValues()
    }
}
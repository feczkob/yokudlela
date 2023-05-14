package com.yokudlela.kitchen.controller.rest

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.yokudlela.kitchen.application.AspectLogger
import com.yokudlela.kitchen.application.GenericConverter
import com.yokudlela.kitchen.business.config.OrderItemConfig
import com.yokudlela.kitchen.business.model.Status
import com.yokudlela.kitchen.controller.Constants
import com.yokudlela.kitchen.controller.Constants.Companion.ALL
import com.yokudlela.kitchen.controller.Constants.Companion.MODIFY
import com.yokudlela.kitchen.controller.Constants.Companion.ORDER_ITEM_ID
import com.yokudlela.kitchen.controller.Constants.Companion.STATUS
import com.yokudlela.kitchen.controller.converter.OrderItemToBusinessConverter
import com.yokudlela.kitchen.controller.error.RestApiError
import com.yokudlela.kitchen.controller.model.request.OrderItemPatch
import com.yokudlela.kitchen.controller.model.request.OrderItemRequest
import com.yokudlela.kitchen.controller.model.response.OrderItemResponse
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
@RequestMapping(Constants.ORDER_ITEM)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@AspectLogger
class OrderItemController(
    private val orderItemConfig: OrderItemConfig,
    private val modelMapper: ModelMapper,
    private val orderItemToBusinessConverter: OrderItemToBusinessConverter,
    private val objectMapper: ObjectMapper,
    private val patchedRequestValidator: PatchedRequestValidator,
    private val converter: GenericConverter,
) {

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "201",
            description = "Created",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = OrderItemResponse::class)
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
        description = "Create an orderItem",
        security = [SecurityRequirement(name = "openid")]
    )
    @PostMapping
    fun registerOrderItem(
        @io.swagger.v3.oas.annotations.parameters.RequestBody
        @Valid @NotNull
        @RequestBody orderItem: OrderItemRequest,
    ): ResponseEntity<OrderItemResponse> {
        val orderItemAdded = orderItemToBusinessConverter.convert(orderItem)
            .addOrderItem()
        val processedUri = UriBuilder.fromResource(OrderItemResponse::class.java)
            .path(ORDER_ITEM_ID)
            .build(orderItemAdded.id)
        return ResponseEntity.created(processedUri).body(
            modelMapper.map(orderItemAdded, OrderItemResponse::class.java)
        )
    }

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = OrderItemResponse::class)
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
        description = "Get an orderItem",
        security = [SecurityRequirement(name = "openid")]
    )
    @GetMapping(ORDER_ITEM_ID)
    fun getOrderItem(
        @Schema(description = "Id of the orderItem to be retrieved", implementation = Int::class)
        @NotNull
        @PathVariable orderItemId: Int,
    ): ResponseEntity<OrderItemResponse> {
        val business = orderItemConfig.getOrderItem()

        return ResponseEntity.ok(
            modelMapper.map(business.loadOrderItem(orderItemId), OrderItemResponse::class.java)
        )
    }

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = OrderItemResponse::class)
            )]
        ), ApiResponse(
            responseCode = "201",
            description = "Created",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = OrderItemResponse::class)
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
        description = "Change an existing - or add a new orderItem",
        security = [SecurityRequirement(name = "openid")]
    )
    @PutMapping(MODIFY + ORDER_ITEM_ID)
    fun modifyOrAddOrderItem(
        @Schema(description = "Id of the orderItem to be modified", implementation = Int::class)
        @NotNull
        @PathVariable orderItemId: Int,
        @io.swagger.v3.oas.annotations.parameters.RequestBody
        @Valid @NotNull
        @RequestBody
        orderItem: OrderItemRequest,
    ): ResponseEntity<OrderItemResponse> {
        val loadedOrderItem = modelMapper.map(
            orderItemToBusinessConverter.convert(orderItem)
                .changeOrAddOrderItem(orderItemId),
            OrderItemResponse::class.java
        )
        val processedUri = UriBuilder.fromResource(OrderItemResponse::class.java)
            .path(ORDER_ITEM_ID)
            .build(loadedOrderItem.id)
        val httpHeaders = HttpHeaders()
        httpHeaders.location = processedUri
        return if (loadedOrderItem.id != orderItemId) {
            ResponseEntity
                .created(processedUri)
                .body(loadedOrderItem)
        } else {
            ResponseEntity
                .ok()
                .headers(httpHeaders)
                .body(loadedOrderItem)
        }
    }

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = OrderItemResponse::class)
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
        description = "Change an existing location",
        security = [SecurityRequirement(name = "openid")]
    )
    @PatchMapping(ORDER_ITEM_ID)
    fun modifyOrderItem(
        @Schema(description = "Id of the orderItem to be modified", implementation = Int::class)
        @NotNull
        @PathVariable
        orderItemId: Int,
        @io.swagger.v3.oas.annotations.parameters.RequestBody
        @NotNull
        @RequestBody
        orderItemRequest: OrderItemPatch,
    ): ResponseEntity<OrderItemResponse> {
        var businessExisting = orderItemConfig.getOrderItem()
        businessExisting = businessExisting.loadOrderItem(orderItemId)
        val restExisting = converter.convert(businessExisting, OrderItemRequest::class.java)

        try {
            objectMapper.updateValue(restExisting, orderItemRequest)
        } catch (exc: JsonMappingException) {
            throw exc
        }

        patchedRequestValidator.validate(
            restExisting,
            "orderItem",
            "modifyOrderItem",
            javaClass,
            Int::class.java,
            OrderItemRequest::class.java
        )

        val business = orderItemConfig.getOrderItem()
        converter.convert(restExisting, business)
        business.id = orderItemId
        business.modify(businessExisting)

        val processedUri = UriBuilder.fromResource(OrderItemResponse::class.java)
            .path(ORDER_ITEM_ID)
            .build(orderItemId)
        val httpHeaders = HttpHeaders()
        httpHeaders.location = processedUri
        val resp = modelMapper.map(business, OrderItemResponse::class.java)

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
        description = "Delete an orderItem",
        security = [SecurityRequirement(name = "openid")]
    )
    @DeleteMapping(ORDER_ITEM_ID)
    fun deleteOrderItem(
        @Schema(description = "Id of the orderItem to be deleted", implementation = Int::class)
        @NotNull
        @PathVariable orderItemId: Int,
    ): ResponseEntity<Any> {
        orderItemConfig.getOrderItem().deleteOrderItem(orderItemId)
        return ResponseEntity.noContent().build()
    }

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = OrderItemResponse::class)
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
        description = "Change an orderItem's status",
        security = [SecurityRequirement(name = "openid")]
    )
    @PutMapping(STATUS + ORDER_ITEM_ID)
    @Deprecated(message = "Not implemented correctly yet")
    fun modifyOrderItemStatus(
        @Schema(description = "Id of the orderItem whose status is to be modified", implementation = Int::class)
        @NotNull
        @PathVariable orderItemId: Int,
        @io.swagger.v3.oas.annotations.parameters.RequestBody
        @Valid @NotNull
        @RequestBody status: Status,
    ): ResponseEntity<OrderItemResponse> {
        val business = orderItemConfig.getOrderItem()
        val loadedOrderItem = modelMapper.map(
            business
                .modifyOrderItemStatus(orderItemId, status),
            OrderItemResponse::class.java
        )
        val processedUri = UriBuilder.fromResource(OrderItemResponse::class.java)
            .path(ORDER_ITEM_ID)
            .build(loadedOrderItem.id)
        val httpHeaders = HttpHeaders()
        httpHeaders.location = processedUri
        return ResponseEntity.ok()
            .headers(httpHeaders)
            .body(loadedOrderItem)
    }

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = ArraySchema(schema = Schema(implementation = OrderItemResponse::class))
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
        description = "Get all orderItems",
        security = [SecurityRequirement(name = "openid")]
    )
    @GetMapping(ALL)
    @ResponseBody
    fun getAllOrderItems(
        @Schema(description = "Number of page to be retrieved", implementation = Int::class, required = false)
        @RequestParam(defaultValue = "0", required = false)
        pageNumber: Int,
        @Schema(description = "Size of page to be retrieved", implementation = Int::class, required = false)
        @RequestParam(defaultValue = "30", required = false)
        pageSize: Int,
    ): List<OrderItemResponse> {
        return orderItemConfig.getOrderItem().loadOrderItems(pageNumber, pageSize)
            .map { modelMapper.map(it, OrderItemResponse::class.java) }
    }
}
package com.yokudlela.kitchen.controller.rest

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.yokudlela.kitchen.application.AspectLogger
import com.yokudlela.kitchen.application.GenericConverter
import com.yokudlela.kitchen.business.config.LocationConfig
import com.yokudlela.kitchen.controller.Constants.Companion.ALL
import com.yokudlela.kitchen.controller.Constants.Companion.LOCATION
import com.yokudlela.kitchen.controller.Constants.Companion.LOCATION_ID
import com.yokudlela.kitchen.controller.converter.LocationToBusinessConverter
import com.yokudlela.kitchen.controller.converter.LocationToInterfaceConverter
import com.yokudlela.kitchen.controller.error.RestApiError
import com.yokudlela.kitchen.controller.model.request.LocationPatch
import com.yokudlela.kitchen.controller.model.request.LocationRequest
import com.yokudlela.kitchen.controller.model.response.LocationResponse
import com.yokudlela.kitchen.controller.validator.PatchedRequestValidator
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import liquibase.pro.packaged.it
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
@RequestMapping(LOCATION)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@com.yokudlela.kitchen.application.AspectLogger
class LocationController(
    private val locationConfig: LocationConfig,
    private val locationToInterfaceConverter: LocationToInterfaceConverter,
    private val locationToBusinessConverter: LocationToBusinessConverter,
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
                schema = Schema(implementation = LocationResponse::class)
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
        description = "Create a location",
        security = [SecurityRequirement(name = "openid")]
    )
    @PostMapping
    fun registerLocation(
        @io.swagger.v3.oas.annotations.parameters.RequestBody
        @Valid @NotNull
        @RequestBody
        location: LocationRequest,
    ): ResponseEntity<LocationResponse> {
        // * https://httpwg.org/specs/rfc9110.html#POST
        val loc = locationToBusinessConverter.convert(location)
            .addLocation()
        val processedUri = UriBuilder.fromResource(LocationResponse::class.java)
            .path(LOCATION_ID)
            .build(loc.id)
        return ResponseEntity.created(processedUri)
            .body(locationToInterfaceConverter.convert(loc))
    }

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = LocationResponse::class)
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
            description = "Not Found",
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
        description = "Get a location",
        security = [SecurityRequirement(name = "openid")]
    )
    @GetMapping(LOCATION_ID)
    fun getLocation(
        @Schema(description = "Id of the location to be retrieved", implementation = Int::class)
        @NotNull
        @PathVariable
        locationId: Int,
    ): ResponseEntity<LocationResponse> {
        // * https://httpwg.org/specs/rfc9110.html#GET
        val business = locationConfig.getLocation()

        return ResponseEntity.ok()
            .body(
                locationToInterfaceConverter.convert(
                    business.loadLocation(locationId)
                )
            )
    }

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = LocationResponse::class)
            )]
        ), ApiResponse(
            responseCode = "201",
            description = "Created",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = LocationResponse::class)
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
        description = "Change an existing - or add a new location",
        security = [SecurityRequirement(name = "openid")]
    )
    @PutMapping(LOCATION_ID)
    fun modifyOrCreateLocation(
        @Schema(description = "Id of the location to be modified", implementation = Int::class)
        @NotNull
        @PathVariable
        locationId: Int,
        @io.swagger.v3.oas.annotations.parameters.RequestBody
        @Valid @NotNull
        @RequestBody
        location: LocationRequest,
    ): ResponseEntity<LocationResponse> {
        // * https://httpwg.org/specs/rfc9110.html#PUT
        val loadedLocation = locationToInterfaceConverter.convert(
            locationToBusinessConverter.convert(location)
                .changeOrAddLocation(locationId)
        )
        val processedUri = UriBuilder.fromResource(LocationResponse::class.java)
            .path(LOCATION_ID)
            .build(loadedLocation.id)
        val httpHeaders = HttpHeaders()
        httpHeaders.location = processedUri
        return if (loadedLocation.id != locationId) {
            ResponseEntity
                .created(processedUri)
                .body(loadedLocation)
        } else {
            ResponseEntity
                .ok()
                .headers(httpHeaders)
                .body(loadedLocation)
        }
    }

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = LocationResponse::class)
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
    @PatchMapping(LOCATION_ID)
    fun modifyLocation(
        @Schema(description = "Id of the location to be modified", implementation = Int::class)
        @NotNull
        @PathVariable
        locationId: Int,
        @io.swagger.v3.oas.annotations.parameters.RequestBody
        @NotNull
        @RequestBody
        locationRequest: LocationPatch,
    ): ResponseEntity<LocationResponse> {
        var businessExisting = locationConfig.getLocation()
        businessExisting = businessExisting.loadLocation(locationId)
        val restExisting = converter.convert(businessExisting, LocationRequest::class.java)

        try {
            objectMapper.updateValue(restExisting, locationRequest)
        } catch (exc: JsonMappingException) {
            throw exc
        }

        patchedRequestValidator.validate(
            restExisting,
            "location",
            "modifyLocation",
            javaClass,
            Int::class.java,
            LocationRequest::class.java
        )

        val business = locationConfig.getLocation()
        converter.convert(restExisting, business)
        business.id = locationId
        business.modify(businessExisting)

        val processedUri = UriBuilder.fromResource(LocationResponse::class.java)
            .path(LOCATION_ID)
            .build(locationId)
        val httpHeaders = HttpHeaders()
        httpHeaders.location = processedUri
        val resp = locationToInterfaceConverter.convert(business)

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
        description = "Delete a location",
        security = [SecurityRequirement(name = "openid")]
    )
    @DeleteMapping(LOCATION_ID)
    fun deleteLocation(
        @Schema(description = "Id of the location to be deleted", implementation = Int::class)
        @NotNull
        @PathVariable
        locationId: Int,
    ): ResponseEntity<Any> {
        // * https://httpwg.org/specs/rfc9110.html#DELETE
        locationConfig.getLocation().deleteLocation(locationId)
        return ResponseEntity.noContent().build()
    }

    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = ArraySchema(schema = Schema(implementation = LocationResponse::class))
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
        description = "Get all locations",
        security = [SecurityRequirement(name = "openid")]
    )
    @GetMapping(ALL)
    @ResponseBody
    fun getAllLocations(
        @Schema(description = "Number of page to be retrieved", implementation = Int::class, required = false)
        @RequestParam(defaultValue = "0", required = false)
        pageNumber: Int,
        @Schema(description = "Size of page to be retrieved", implementation = Int::class, required = false)
        @RequestParam(defaultValue = "30", required = false)
        pageSize: Int,
    ): List<LocationResponse> {
        // * https://httpwg.org/specs/rfc9110.html#GET
        val locations = locationConfig.getLocation().loadLocations(pageNumber, pageSize)
        return locations.map { locationToInterfaceConverter.convert(it) }
    }
}
package hu.soft4d.yokudlela3.controller.dish;

import static hu.soft4d.yokudlela3.controller.ControllerConstant.DISH_PATH;
import static hu.soft4d.yokudlela3.controller.ControllerConstant.PAGE_PARAM_DEFAULT_VALUE;
import static hu.soft4d.yokudlela3.controller.ControllerConstant.PAGE_PARAM_DESCRIPTION;
import static hu.soft4d.yokudlela3.controller.ControllerConstant.SIZE_PARAM_DEFAULT_VALUE;
import static hu.soft4d.yokudlela3.controller.ControllerConstant.SIZE_PARAM_DESCRIPTION;
import static hu.soft4d.yokudlela3.controller.ControllerConstant.SLASH;

import hu.soft4d.yokudlela3.application.RoleConstant;
import hu.soft4d.yokudlela3.application.annotation.MethodLogging;
import hu.soft4d.yokudlela3.application.error.ApiError;
import hu.soft4d.yokudlela3.business.component.PatchHelper;
import hu.soft4d.yokudlela3.business.model.dish.model.Dish;
import hu.soft4d.yokudlela3.controller.ControllerConstant;
import hu.soft4d.yokudlela3.controller.bean.GeneralValidator;
import hu.soft4d.yokudlela3.controller.dish.model.request.DishRequest;
import hu.soft4d.yokudlela3.controller.dish.model.response.DishListResponse;
import hu.soft4d.yokudlela3.controller.dish.model.response.DishResponse;
import hu.soft4d.yokudlela3.controller.filter.HttpHeaderHelper;
import hu.soft4d.yokudlela3.controller.model.response.IdResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.modelmapper.ModelMapper;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Tag(name = "Dailymenu Dish API", description = "Dish API of Yokudlela 3 Daily Menu Module")
@Slf4j
@MethodLogging
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(DISH_PATH)
public class DishController {

    public static final String GET_ALL_ACTIVE_DISHES = SLASH + "findByActiveEqualsTrue";
    public static final String GET_DISH_BY_NAME_AND_ACTIVE = SLASH + "findByNameAndActive";
    public static final String GET_DISH_BY_ID = SLASH + ControllerConstant.ID_PARAM;


    @Inject
    ModelMapper modelMapper;

    @Inject
    HttpHeaderHelper headerHelper;

    @Inject
    PatchHelper patchHelper;

    @Inject
    GeneralValidator<Object> validator;

    @Inject
    Dish dish;


    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESS, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESS, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = DishListResponse.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_NOT_FOUND, description = ControllerConstant.API_DOC_RESPONSE_DESC_NOT_FOUND, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Getting all existing dishes")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE})
    @GET
    public DishListResponse getAllDishes(
            @Parameter(description = PAGE_PARAM_DESCRIPTION, required = false, example = PAGE_PARAM_DEFAULT_VALUE)
            @QueryParam("page")
            @DefaultValue(PAGE_PARAM_DEFAULT_VALUE)
                    String page,
            @Parameter(description = SIZE_PARAM_DESCRIPTION, required = false, example = SIZE_PARAM_DEFAULT_VALUE)
            @QueryParam("size")
            @DefaultValue(SIZE_PARAM_DEFAULT_VALUE)
                    String size
    ) {
        return convertDishListResponse(dish.loadAll(page, size));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESS, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESS, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = DishListResponse.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_NOT_FOUND, description = ControllerConstant.API_DOC_RESPONSE_DESC_NOT_FOUND, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Getting only active dishes")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE, RoleConstant.MANAGER_ROLE})
    @GET
    @Path(GET_ALL_ACTIVE_DISHES)
    public DishListResponse getAllActiveDishes(
            @Parameter(description = PAGE_PARAM_DESCRIPTION, required = false, example = PAGE_PARAM_DEFAULT_VALUE)
            @QueryParam("page")
            @DefaultValue(PAGE_PARAM_DEFAULT_VALUE)
                    String page,
            @Parameter(description = SIZE_PARAM_DESCRIPTION, required = false, example = SIZE_PARAM_DEFAULT_VALUE)
            @QueryParam("size")
            @DefaultValue(SIZE_PARAM_DEFAULT_VALUE)
                    String size
    ) {
        return convertDishListResponse(dish.findAllActive(page, size));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESS, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESS, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = DishListResponse.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_NOT_FOUND, description = ControllerConstant.API_DOC_RESPONSE_DESC_NOT_FOUND, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Searching dish by ID")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE, RoleConstant.MANAGER_ROLE})
    @GET
    @Path(GET_DISH_BY_NAME_AND_ACTIVE)
    public DishListResponse getDishByNameAndActive(
            @Parameter(description = "Name query param", required = false, example = "bab")
            @QueryParam("name")
                    String name,
            @Parameter(description = "Status/Active query param", required = false, example = "true")
            @QueryParam("active")
                    Boolean active,
            @Parameter(description = PAGE_PARAM_DESCRIPTION, required = false, example = PAGE_PARAM_DEFAULT_VALUE)
            @QueryParam("page")
            @DefaultValue(PAGE_PARAM_DEFAULT_VALUE)
                    String page,
            @Parameter(description = SIZE_PARAM_DESCRIPTION, required = false, example = SIZE_PARAM_DEFAULT_VALUE)
            @QueryParam("size")
            @DefaultValue(SIZE_PARAM_DEFAULT_VALUE)
                    String size
    ) {
        return convertDishListResponse(dish.findByNameAndActive(name, active, page, size));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESS, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESS, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = DishResponse.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_FORBIDDEN, description = ControllerConstant.API_DOC_RESPONSE_DESC_FORBIDDEN, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Searching dish by ID")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE, RoleConstant.MANAGER_ROLE})
    @GET
    @Path(GET_DISH_BY_ID)
    public DishResponse getDishById(
            @Parameter(description = "Unique identifier of dish", required = true, example = "UUID")
            @PathParam(ControllerConstant.ID)
                    UUID id
    ) {
        dish.setId(id);
        return convertDishResponse(dish.load());
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESSFULLY_CREATED, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESSFULLY_CREATED, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Creating new dish")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE})
    @POST
    public Response postDish(
            @Context
                    UriInfo uriInfo,
            @Parameter(description = "New dish", required = true)
            @Valid
                    DishRequest dishRequest
    ) {
        modelMapper.map(dishRequest, dish);
        dish.setId(null);
        final IdResponse idResponse = IdResponse.builder()
                .id(dish.save().getId())
                .build();
        return Response.created(uriInfo.getAbsolutePathBuilder()
                        .path(idResponse.getId().toString())
                        .build())
                .build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESS, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESS, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESSFULLY_CREATED, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESSFULLY_CREATED, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Updating existing dish if exists, otherwise create.")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE})
    @PUT
    @Path(GET_DISH_BY_ID)
    public Response putDish(
            @Context
                    UriInfo uriInfo,
            @Parameter(description = "Unique identifier of menu", required = true, example = "UUID")
            @PathParam(ControllerConstant.ID)
                    UUID id,
            @Parameter(description = "Dish with all attributes (except ID)", required = true)
            @Valid
                    DishRequest dishRequest
    ) {
        modelMapper.map(dishRequest, dish);
        dish.setId(id);
        final DishResponse dishResponse = convertDishResponse(dish.save());

        URI location = headerHelper.getLocation(uriInfo);
        return id.equals(dishResponse.getContent().getId())
                ? Response.ok(dishResponse).location(location).build()
                : Response.created(location).entity(dishResponse).build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESS, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESS, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_NOT_FOUND, description = ControllerConstant.API_DOC_RESPONSE_DESC_NOT_FOUND, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Updating existing dish attribute(s).")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE})
    @PATCH
    @Path(GET_DISH_BY_ID)
    public Response patchDish(
            @Context
                    UriInfo uriInfo,
            @Parameter(description = "Unique identifier of menu", required = true, example = "UUID")
            @org.jboss.resteasy.annotations.jaxrs.PathParam(ControllerConstant.ID)
                    UUID id,
            @Parameter(description = "Dish with attributes optionally (except ID)", required = true)
                    DishRequest dishPatchRequest
    ) {
        dish.setId(id);
        dish.load();
        patchHelper.merge(dish, dishPatchRequest);
        modelMapper.map(dish, dishPatchRequest);
        validator.validate(dishPatchRequest);

        final DishResponse dishResponse = DishResponse.builder()
                .content(dish.save())
                .build();
        return Response.ok(dishResponse)
                .location(headerHelper.getLocation(uriInfo))
                .build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESSFULLY_DELETED, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESSFULLY_DELETED, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_NOT_FOUND, description = ControllerConstant.API_DOC_RESPONSE_DESC_NOT_FOUND, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Deleting existing dish")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE})
    @DELETE
    @Path(GET_DISH_BY_ID)
    public Response deleteDish(
            @Parameter(description = "Unique identifier of menu", required = true, example = "UUID")
            @org.jboss.resteasy.annotations.jaxrs.PathParam(ControllerConstant.ID)
                    UUID id
    ) {
        dish.setId(id);
        dish.delete();
        return Response.noContent().build();
    }

    private DishResponse convertDishResponse(Dish dish) {
        return DishResponse.builder()
                .content(dish)
                .build();
    }

    private DishListResponse convertDishListResponse(List<Dish> list) {
        return DishListResponse.builder()
                .contents(list)
                .build();
    }
}

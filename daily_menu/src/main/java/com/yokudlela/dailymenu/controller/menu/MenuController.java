package com.yokudlela.dailymenu.controller.menu;

import com.yokudlela.dailymenu.application.RoleConstant;
import com.yokudlela.dailymenu.application.error.ApiError;
import com.yokudlela.dailymenu.business.component.PatchHelper;
import com.yokudlela.dailymenu.business.model.menu.model.Menu;
import com.yokudlela.dailymenu.controller.ControllerConstant;
import com.yokudlela.dailymenu.controller.bean.GeneralValidator;
import com.yokudlela.dailymenu.controller.filter.HttpHeaderHelper;
import com.yokudlela.dailymenu.controller.menu.model.request.MenuRequest;
import com.yokudlela.dailymenu.controller.menu.model.response.MenuListResponse;
import com.yokudlela.dailymenu.controller.menu.model.response.MenuResponse;
import com.yokudlela.dailymenu.controller.model.response.IdResponse;
import com.yokudlela.dailymenu.application.annotation.MethodLogging;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.modelmapper.ModelMapper;

import java.net.URI;
import java.time.LocalDate;
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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Tag(name = "Dailymenu Menu API", description = "Menu API of Yokudlela 3 Daily Menu Module")
@Slf4j
@MethodLogging
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(ControllerConstant.MENU_PATH)
public class MenuController {

    public static final String GET_MENU_BY_DAY = ControllerConstant.SLASH + "findMenuByDay";
    public static final String GET_MENU_BETWEEN_DAYS = ControllerConstant.SLASH + "findMenusBetweenDayFromDayTo";
    public static final String GET_MENU_BY_ID = ControllerConstant.SLASH + ControllerConstant.ID_PARAM;

    @Inject
    ModelMapper modelMapper;

    @Inject
    HttpHeaderHelper headerHelper;

    @Inject
    PatchHelper patchHelper;

    @Inject
    GeneralValidator<Object> validator;

    @Inject
    Menu menu;


    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESS, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESS, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MenuResponse.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_NOT_FOUND, description = ControllerConstant.API_DOC_RESPONSE_DESC_NOT_FOUND, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Getting all existing menus")
    @GET
    public MenuListResponse getAllMenus(
            @Parameter(description = ControllerConstant.PAGE_PARAM_DESCRIPTION, required = false, example = ControllerConstant.PAGE_PARAM_DEFAULT_VALUE)
            @QueryParam("page")
            @DefaultValue(ControllerConstant.PAGE_PARAM_DEFAULT_VALUE)
                    String page,
            @Parameter(description = ControllerConstant.SIZE_PARAM_DESCRIPTION, required = false, example = ControllerConstant.SIZE_PARAM_DEFAULT_VALUE)
            @QueryParam("size")
            @DefaultValue(ControllerConstant.SIZE_PARAM_DEFAULT_VALUE)
                    String size
    ) {
        return convertMenuListResponse(menu.loadAll(page, size));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESS, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESS, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MenuResponse.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_NOT_FOUND, description = ControllerConstant.API_DOC_RESPONSE_DESC_NOT_FOUND, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Searching menu by day")
    @GET
    @Path(GET_MENU_BY_DAY)
    public MenuResponse getMenuByDay(
            @Parameter(description = "Day when daily menu will be alive ", example = "2023-01-02", required = true)
            @QueryParam("day")
                    LocalDate day
    ) {
        return convertMenuResponse(menu.findMenuByDay(day));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESS, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESS, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MenuListResponse.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_NOT_FOUND, description = ControllerConstant.API_DOC_RESPONSE_DESC_NOT_FOUND, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Searching menu by day interval")
    @GET
    @Path(GET_MENU_BETWEEN_DAYS)
    public MenuListResponse getMenusBetweenDayFromDayTo(
            @Parameter(description = "Day when daily menu will be alive - start of the interval", example = "2023-01-02", required = true)
            @QueryParam("fromDay")
                    LocalDate fromDay,
            @Parameter(description = "Day when daily menu will be alive - end of the interval", example = "2023-01-11", required = true)
            @QueryParam("toDay")
                    LocalDate toDay,
            @Parameter(description = ControllerConstant.PAGE_PARAM_DESCRIPTION, required = false, example = ControllerConstant.PAGE_PARAM_DEFAULT_VALUE)
            @QueryParam("page")
            @DefaultValue(ControllerConstant.PAGE_PARAM_DEFAULT_VALUE)
                    String page,
            @Parameter(description = ControllerConstant.SIZE_PARAM_DESCRIPTION, required = false, example = ControllerConstant.SIZE_PARAM_DEFAULT_VALUE)
            @QueryParam("size")
            @DefaultValue(ControllerConstant.SIZE_PARAM_DEFAULT_VALUE)
                    String size
    ) {
        return convertMenuListResponse(menu.findMenusBetweenDayFromDayTo(fromDay, toDay, page, size));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESS, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESS, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MenuResponse.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_NOT_FOUND, description = ControllerConstant.API_DOC_RESPONSE_DESC_NOT_FOUND, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Searching menu by ID")
    @GET
    @Path(GET_MENU_BY_ID)
    public MenuResponse getMenuById(
            @Parameter(description = "Unique identifier of menu", required = true, example = "UUID")
            @PathParam(ControllerConstant.ID)
                    UUID id
    ) {
        menu.setId(id);
        return convertMenuResponse(menu.load());
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESSFULLY_CREATED, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESSFULLY_CREATED, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Creating new menu")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE, RoleConstant.MANAGER_ROLE})
    @POST
    public Response postMenu(
            @Context
                    UriInfo uriInfo,
            @Parameter(description = "New menu", required = true)
            @Valid
            MenuRequest menuRequest
    ) {
        modelMapper.map(menuRequest, menu);
        menu.setId(null);
        final IdResponse idResponse = IdResponse.builder()
                .id(menu.save().getId())
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
    @Operation(summary = "Updating existing menu if exists, otherwise create.")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE, RoleConstant.MANAGER_ROLE})
    @PUT
    @Path(ControllerConstant.SLASH + ControllerConstant.ID_PARAM)
    public Response putMenu(
            @Context
                    UriInfo uriInfo,
            @Parameter(description = "Unique identifier of menu", required = true, example = "UUID")
            @PathParam(ControllerConstant.ID)
                    UUID id,
            @Parameter(description = "Menu with all attributes (except ID)", required = true)
            @Valid
                    MenuRequest menuRequest
    ) {
        modelMapper.map(menuRequest, menu);
        menu.setId(id);
        final MenuResponse menuResponse = MenuResponse.builder()
                .content(menu.save())
                .build();

        URI location = headerHelper.getLocation(uriInfo);
        return id.equals(menuResponse.getContent().getId())
                ? Response.ok(menuResponse).location(location).build()
                : Response.created(location).entity(menuResponse).build();
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
    @Operation(summary = "Updating existing menu attribute(s).")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE, RoleConstant.MANAGER_ROLE})
    @PATCH
    @Path(ControllerConstant.SLASH + ControllerConstant.ID_PARAM)
    public Response patchMenu(
            @Context
                    UriInfo uriInfo,
            @Parameter(description = "Unique identifier of menu", required = true, example = "UUID")
            @PathParam(ControllerConstant.ID)
                    UUID id,
            @Parameter(description = "Menu with attributes optionally (except ID)", required = true)
                    MenuRequest menuPatchRequest
    ) {
        menu.setId(id);
        menu.load();
        patchHelper.merge(menu, menuPatchRequest);
        modelMapper.map(menu, menuPatchRequest);
        validator.validate(menuPatchRequest);

        final MenuResponse menuResponse = MenuResponse.builder()
                .content(menu.save())
                .build();
        return Response.ok(menuResponse)
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
    @Operation(summary = "Deleting existing menu")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE, RoleConstant.MANAGER_ROLE})
    @DELETE
    @Path(ControllerConstant.SLASH + ControllerConstant.ID_PARAM)
    public Response deleteMenu(
            @Parameter(description = "Unique identifier of menu", required = true, example = "UUID")
            @PathParam(ControllerConstant.ID)
                    UUID id
    ) {
        menu.setId(id);
        menu.delete();
        return Response.noContent().build();
    }

    private MenuResponse convertMenuResponse(Menu menu) {
        return MenuResponse.builder()
                .content(menu)
                .build();
    }

    private MenuListResponse convertMenuListResponse(List<Menu> list) {
        return MenuListResponse.builder()
                .contents(list)
                .build();
    }
}

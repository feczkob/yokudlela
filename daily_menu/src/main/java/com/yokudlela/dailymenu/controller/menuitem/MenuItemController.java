package com.yokudlela.dailymenu.controller.menuitem;

import com.yokudlela.dailymenu.application.RoleConstant;
import com.yokudlela.dailymenu.business.model.menuitem.model.MenuItem;
import com.yokudlela.dailymenu.controller.ControllerConstant;
import com.yokudlela.dailymenu.controller.filter.HttpHeaderHelper;
import com.yokudlela.dailymenu.controller.menuitem.model.request.MenuItemAmountRelatedRequest;
import com.yokudlela.dailymenu.controller.menuitem.model.request.MenuItemRequest;
import com.yokudlela.dailymenu.controller.menuitem.model.response.MenuItemCategoryListResponse;
import com.yokudlela.dailymenu.controller.menuitem.model.response.MenuItemListResponse;
import com.yokudlela.dailymenu.controller.menuitem.model.response.MenuItemResponse;
import com.yokudlela.dailymenu.controller.menuitem.model.response.MenuItemSectionListResponse;
import com.yokudlela.dailymenu.controller.menuitem.model.response.MenuItemVariantListResponse;
import com.yokudlela.dailymenu.controller.model.response.IdResponse;
import com.yokudlela.dailymenu.application.annotation.MethodLogging;
import com.yokudlela.dailymenu.application.error.ApiError;
import com.yokudlela.dailymenu.business.component.PatchHelper;
import com.yokudlela.dailymenu.controller.bean.GeneralValidator;

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

@Tag(name = "Dailymenu Menu Item API", description = "Menu item API of Yokudlela 3 Daily Menu Module")
@Slf4j
@MethodLogging
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(ControllerConstant.MENU_ITEM_PATH)
public class MenuItemController {

    @Inject
    ModelMapper modelMapper;

    @Inject
    HttpHeaderHelper headerHelper;

    @Inject
    PatchHelper patchHelper;

    @Inject
    GeneralValidator<Object> validator;

    @Inject
    MenuItem menuItem;


    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESS, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESS, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MenuItemResponse.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_NOT_FOUND, description = ControllerConstant.API_DOC_RESPONSE_DESC_NOT_FOUND, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Getting all existing menu items")
    @GET
    public MenuItemListResponse getAllMenuItems(
            @Parameter(description = ControllerConstant.PAGE_PARAM_DESCRIPTION, required = false, example = ControllerConstant.PAGE_PARAM_DEFAULT_VALUE)
            @QueryParam("page")
            @DefaultValue(ControllerConstant.PAGE_PARAM_DEFAULT_VALUE)
                    String page,
            @Parameter(description = ControllerConstant.SIZE_PARAM_DESCRIPTION, required = false, example = ControllerConstant.SIZE_PARAM_DEFAULT_VALUE)
            @QueryParam("size")
            @DefaultValue(ControllerConstant.SIZE_PARAM_DEFAULT_VALUE)
                    String size
    ) {
        return convertMenuItemListResponse(menuItem.loadAll(page, size));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESS, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESS, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MenuItemListResponse.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_NOT_FOUND, description = ControllerConstant.API_DOC_RESPONSE_DESC_NOT_FOUND, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Searching menu item by day")
    @GET
    @Path(ControllerConstant.SLASH + "findMenuItemByDay")
    public MenuItemListResponse getMenuItemByDay(
            @Parameter(description = "Day when daily menu items will be alive ", example = "2023-01-02", required = true)
            @QueryParam("day")
            LocalDate day,
            @Parameter(description = ControllerConstant.PAGE_PARAM_DESCRIPTION, required = false, example = ControllerConstant.PAGE_PARAM_DEFAULT_VALUE)
            @QueryParam("page")
            @DefaultValue(ControllerConstant.PAGE_PARAM_DEFAULT_VALUE)
                    String page,
            @Parameter(description = ControllerConstant.SIZE_PARAM_DESCRIPTION, required = false, example = ControllerConstant.SIZE_PARAM_DEFAULT_VALUE)
            @QueryParam("size")
            @DefaultValue(ControllerConstant.SIZE_PARAM_DEFAULT_VALUE)
                    String size
    ) {
        return convertMenuItemListResponse(menuItem.findMenuItemsByDay(day, page, size));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESS, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESS, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MenuItemListResponse.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_NOT_FOUND, description = ControllerConstant.API_DOC_RESPONSE_DESC_NOT_FOUND, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Searching menu item by day")
    @GET
    @Path(ControllerConstant.SLASH + "findMenuItemsDayFromDayTo")
    public MenuItemListResponse getMenuItemsDayFromDayTo(
            @Parameter(description = "Day when daily menu items will be alive - start of the interval", example = "2023-01-02", required = true)
            @QueryParam("fromDay")
            LocalDate fromDay,
            @Parameter(description = "Day when daily menu items will be alive - end of the interval", example = "2023-01-11", required = true)
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
        return convertMenuItemListResponse(menuItem.findMenuItemsBetweenDayFromDayTo(fromDay, toDay, page, size));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESS, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESS, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MenuItemResponse.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_NOT_FOUND, description = ControllerConstant.API_DOC_RESPONSE_DESC_NOT_FOUND, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Searching menu item by ID")
    @GET
    @Path(ControllerConstant.SLASH + ControllerConstant.ID_PARAM)
    public MenuItemResponse getMenuById(
            @Parameter(description = "Unique identifier of menu item", required = true, example = "UUID")
            @PathParam(ControllerConstant.ID)
                    UUID id
    ) {
        menuItem.setId(id);
        return convertMenuItemResponse(menuItem.load());
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESSFULLY_CREATED, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESSFULLY_CREATED, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Creating new menu item")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE, RoleConstant.MANAGER_ROLE})
    @POST
    public Response postMenuItem(
            @Context
                    UriInfo uriInfo,
            @Parameter(description = "New menu item", required = true)
            @Valid
            MenuItemRequest menuItemRequest
    ) {
        modelMapper.map(menuItemRequest, menuItem);
        menuItem.setId(null);
        final IdResponse idResponse = IdResponse.builder()
                .id(menuItem.save().getId())
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
    @Operation(summary = "Updating existing menu item if exists, otherwise create.")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE, RoleConstant.MANAGER_ROLE})
    @PUT
    @Path(ControllerConstant.SLASH + ControllerConstant.ID_PARAM)
    public Response putMenuItem(
            @Context
                    UriInfo uriInfo,
            @Parameter(description = "Unique identifier of menu item", required = true, example = "UUID")
            @PathParam(ControllerConstant.ID)
                    UUID id,
            @Parameter(description = "Menu item with all attributes (except ID)", required = true)
            @Valid
                    MenuItemRequest menuItemRequest
    ) {
        modelMapper.map(menuItemRequest, menuItem);
        menuItem.setId(id);
        final MenuItemResponse menuItemResponse = convertMenuItemResponse(menuItem.save());

        URI location = headerHelper.getLocation(uriInfo);
        return id.equals(menuItemResponse.getContent().getId())
                ? Response.ok(menuItemResponse).location(location).build()
                : Response.created(location).entity(menuItemResponse).build();
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
    @Operation(summary = "Updating existing menu item attribute(s).")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE, RoleConstant.MANAGER_ROLE})
    @PATCH
    @Path(ControllerConstant.SLASH + ControllerConstant.ID_PARAM)
    public Response patchMenuItem(
            @Context
                    UriInfo uriInfo,
            @Parameter(description = "Unique identifier of menu item", required = true, example = "UUID")
            @PathParam(ControllerConstant.ID)
                    UUID id,
            @Parameter(description = "Menu item with attributes optionally (except ID)", required = true)
                    MenuItemRequest menuItemPatchRequest
    ) {
        menuItem.setId(id);
        menuItem.load();
        patchHelper.merge(menuItem, menuItemPatchRequest);
        modelMapper.map(menuItem, menuItemPatchRequest);
        validator.validate(menuItemPatchRequest);

        final MenuItemResponse menuItemResponse = MenuItemResponse.builder()
                .content(menuItem.save())
                .build();
        return Response.ok(menuItemResponse)
                .location(headerHelper.getLocation(uriInfo))
                .build();
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
    @Operation(summary = "Requesting given portion from existing menu item (for kitchen crew).")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE, RoleConstant.MANAGER_ROLE, RoleConstant.DISH_CLAIM_REQUEST_ROLE})
    @PATCH
    @Path(ControllerConstant.SLASH + "claim" + ControllerConstant.SLASH + ControllerConstant.ID_PARAM)
    public Response patchMenuItemMakingClaim(
            @Context
                    UriInfo uriInfo,
            @Parameter(description = "Unique identifier of menu item", required = true, example = "UUID")
            @PathParam(ControllerConstant.ID)
                    UUID id,
            @Parameter(description = "Menu item with attributes optionally (except ID)", required = true)
            @Valid
            MenuItemAmountRelatedRequest request
    ) {
        menuItem.setId(id);
        final MenuItemResponse menuItemResponse = convertMenuItemResponse(menuItem.claim(request.getAmount()));
        return Response.ok(menuItemResponse)
                .location(uriInfo.getRequestUri())
                .build();
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
    @Operation(summary = "Reporting given portion from existing menu item has already been cooked (by kitchen crew).")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE, RoleConstant.MANAGER_ROLE, RoleConstant.DISH_CLAIM_RESPONSE_ROLE})
    @PATCH
    @Path(ControllerConstant.SLASH + "done" + ControllerConstant.SLASH + ControllerConstant.ID_PARAM)
    public Response patchMenuItemMakingFinished(
            @Context
                    UriInfo uriInfo,
            @Parameter(description = "Unique identifier of menu item", required = true, example = "UUID")
            @PathParam(ControllerConstant.ID)
                    UUID id,
            @Parameter(description = "Menu item with attributes optionally (except ID)", required = true)
            @Valid
                    MenuItemAmountRelatedRequest request
    ) {
        menuItem.setId(id);
        final MenuItemResponse menuItemResponse = convertMenuItemResponse(menuItem.done(request.getAmount()));
        return Response.ok(menuItemResponse)
                .location(uriInfo.getRequestUri())
                .build();
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
    @Operation(summary = "Reporting given portion from existing menu item has already been purchased.")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE, RoleConstant.MANAGER_ROLE, RoleConstant.DISH_PAID_ROLE})
    @PATCH
    @Path(ControllerConstant.SLASH + "pay" + ControllerConstant.SLASH + ControllerConstant.ID_PARAM)
    public Response patchMenuItemAsPaid(
            @Context
                    UriInfo uriInfo,
            @Parameter(description = "Unique identifier of menu item", required = true, example = "UUID")
            @PathParam(ControllerConstant.ID)
                    UUID id,
            @Parameter(description = "Menu item with attributes optionally (except ID)", required = true)
            @Valid
                    MenuItemAmountRelatedRequest request
    ) {
        menuItem.setId(id);
        final MenuItemResponse menuItemResponse = convertMenuItemResponse(menuItem.pay(request.getAmount()));
        return Response.ok(menuItemResponse)
                .location(uriInfo.getRequestUri())
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
    @Operation(summary = "Deleting existing menu item")
    @RolesAllowed( {RoleConstant.ADMIN_ROLE, RoleConstant.MANAGER_ROLE})
    @DELETE
    @Path(ControllerConstant.SLASH + ControllerConstant.ID_PARAM)
    public Response deleteMenuItem(
            @Parameter(description = "Unique identifier of menu item", required = true, example = "UUID")
            @PathParam(ControllerConstant.ID)
                    UUID id
    ) {
        menuItem.setId(id);
        menuItem.delete();
        return Response.noContent().build();
    }

    private MenuItemResponse convertMenuItemResponse(MenuItem menuItem) {
        return MenuItemResponse.builder()
                .content(menuItem)
                .build();
    }

    private MenuItemListResponse convertMenuItemListResponse(List<MenuItem> list) {
        return MenuItemListResponse.builder()
                .contents(list)
                .build();
    }

    // --- ENUMS ---------------------------

    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESS, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESS, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MenuItemCategoryListResponse.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_NOT_FOUND, description = ControllerConstant.API_DOC_RESPONSE_DESC_NOT_FOUND, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Getting all existing menu item categories")
    @Path(ControllerConstant.MENU_ITEM_ENUM_PATH + ControllerConstant.SLASH + "category")
    @GET
    public MenuItemCategoryListResponse getAllMenuItemCategories() {
        return MenuItemCategoryListResponse.builder()
                .contents(menuItem.provideAllCategories())
                .build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESS, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESS, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MenuItemSectionListResponse.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_NOT_FOUND, description = ControllerConstant.API_DOC_RESPONSE_DESC_NOT_FOUND, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Getting all existing menu item sections")
    @Path(ControllerConstant.MENU_ITEM_ENUM_PATH + ControllerConstant.SLASH + "section")
    @GET
    public MenuItemSectionListResponse getAllMenuItemSections() {
        return MenuItemSectionListResponse.builder()
                .contents(menuItem.provideAllSections())
                .build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_SUCCESS, description = ControllerConstant.API_DOC_RESPONSE_DESC_SUCCESS, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MenuItemVariantListResponse.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_BAD_REQUEST, description = ControllerConstant.API_DOC_RESPONSE_DESC_BAD_REQUEST, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_NOT_FOUND, description = ControllerConstant.API_DOC_RESPONSE_DESC_NOT_FOUND, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = ControllerConstant.API_DOC_RESPONSE_CODE_INTERNAL_ERROR, description = ControllerConstant.API_DOC_RESPONSE_DESC_INTERNAL_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class))})
    })
    @Operation(summary = "Getting all existing menu item variants")
    @Path(ControllerConstant.MENU_ITEM_ENUM_PATH + ControllerConstant.SLASH + "variant")
    @GET
    public MenuItemVariantListResponse getAllMenuItemVariants() {
        return MenuItemVariantListResponse.builder()
                .contents(menuItem.provideAllVariants())
                .build();
    }

}

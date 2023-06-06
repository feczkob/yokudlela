package com.yokudlela.dailymenu.integration.test.v1.testcontainers;

import com.yokudlela.dailymenu.business.model.dish.model.Dish;
import com.yokudlela.dailymenu.client.persistence.entity.enumeration.Category;
import com.yokudlela.dailymenu.integration.IntegrationTestHelper;
import com.yokudlela.dailymenu.application.annotation.MethodLogging;
import com.yokudlela.dailymenu.controller.ControllerConstant;
import com.yokudlela.dailymenu.controller.dish.DishController;
import com.yokudlela.dailymenu.controller.dish.model.request.DishRequest;
import com.yokudlela.dailymenu.controller.dish.model.response.DishListResponse;
import com.yokudlela.dailymenu.controller.dish.model.response.DishResponse;
import com.yokudlela.dailymenu.integration.BaseIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@MethodLogging
class DishResourceIntegrationTest extends BaseIntegrationTest {


    @BeforeAll
    public static void setup() {
        RestAssured.basePath = ControllerConstant.API_V1 + ControllerConstant.DISH_PATH;
    }

    @Test
    public void givenDish_whenGetAllDishesAsPublicUser_thenUnauthorized() {
        given()
                .when()
                .get()
                .prettyPeek()
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode())
                .contentType(ContentType.JSON);
    }

    @Test
    public void givenDish_whenGetAllDishesAsAdmin_thenOk() {
        getAdminAuthRequest()
                .when()
                .get()
                .prettyPeek()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON);
    }

    @Test
    public void givenDish_whenGetAllDishesAsAdmin_thenReceivedResponseContainsOnlyActiveDishes() {
        final DishListResponse response = getAllAsAdmin();
        assertTrue(response.getContents().stream().allMatch(dish -> dish.getActive()), "Not all elements are active!");
    }

    @Test
    public void givenDishNamePartAndActive_whenGetDishByNameAndActiveAsAdmin_thenReceivedProperResultSet() {
        final DishListResponse all = getAllAsAdmin();
        final Dish sample = all.getContents().get(0);
        final String expectedName = sample.getName();
        final String expectedNamePart = expectedName.substring(1, 5);
        final Boolean expectedActive = null == sample.getActive();
        final long expectedSize = all.getContents().stream().filter(dish ->
                null != expectedActive && expectedActive.equals(dish.getActive())
                        && dish.getName().contains(expectedNamePart)).count();

        final DishListResponse response = getAdminAuthRequest()
                .queryParams(Map.of(
                        IntegrationTestHelper.PATH_PARAM_NAME, expectedNamePart,
                        IntegrationTestHelper.PATH_PARAM_ACTIVE, expectedActive,
                        IntegrationTestHelper.QUERY_PARAM_PAGE, 0, IntegrationTestHelper.QUERY_PARAM_SIZE, 1000))
                .when()
                .get(DishController.GET_DISH_BY_NAME_AND_ACTIVE)
                .prettyPeek()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract()
                .as(DishListResponse.class);
        final List<Dish> result = response.getContents();
        final long filteredResultSized = result.stream().filter(dish ->
                null != expectedActive && expectedActive.equals(dish.getActive())
                        && dish.getName().contains(expectedNamePart)).count();
        assertEquals(expectedSize, filteredResultSized, "Wrong resultset!");
    }

    @Test
    public void givenDishId_whenGetDishByIdAsAdmin_thenReceivedActiveDishWithTheSameId() {
        final UUID expectedDishId = getAllAsAdmin().getContents().get(0).getId();

        final DishResponse response = getAdminAuthRequest()
                .pathParam(ControllerConstant.ID, expectedDishId)
                .when()
                .get(DishController.GET_DISH_BY_ID)
                .prettyPeek()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract()
                .as(DishResponse.class);
        assertEquals(expectedDishId, response.getContent().getId(), "Requested ID not found!");
    }

    @Test
    public void givenNewDish_whenPostDishAsAdmin_thenReceivedNewActiveDishWithTheSameAttributesAndGeneratedId() {
        final DishRequest object = DishRequest.builder()
                .category(Category.soup)
                .name("Eddmeg leves")
                .price(999)
                .recipe(0)
                .build();

        final RequestSpecification request = getAdminAuthRequest();
        final io.restassured.response.Response post = request
                .when()
                .contentType(ContentType.JSON)
                .body(object)
                .post();
        final boolean locationHeaderExists = post.headers().asList().stream().map(Header::getName).anyMatch(
                name -> name.equals("Location"));
        post.prettyPeek()
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode());
        assertTrue(locationHeaderExists, "Location header not found in response!");

        final String locationHeader = post.headers().asList().stream()
                .filter(entry -> "Location".equals(entry.getName()))
                .map(Header::getValue)
                .findFirst().orElse(null);
        final String idAsString = locationHeader.substring(locationHeader.indexOf(RestAssured.basePath) + RestAssured.basePath.length() + 1);
        final UUID uuid = UUID.fromString(idAsString);
        final boolean idIsRealUuid = idAsString.equals(uuid.toString());
        assertTrue(idIsRealUuid, "Valid ID not found!");

        final DishResponse response = getAdminAuthRequest()
                .pathParam(ControllerConstant.ID, uuid)
                .when()
                .get(DishController.GET_DISH_BY_ID)
                .prettyPeek()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract()
                .as(DishResponse.class);
        assertEquals(uuid, response.getContent().getId(), "Requested ID not found!");
    }

    @Test
    public void givenExistingDish_whenPutDishAsAdmin_thenReceivedDishWithTheSameIdAndUpdatedAttributes() {
        final DishListResponse all = getAllAsAdmin();
        final Dish existing = all.getContents().get(0);
        final Category originalCategory = existing.getCategory();
        final Category newCategory = Arrays.stream(Category.values())
                .filter(value -> !value.equals(existing.getCategory()))
                .collect(Collectors.toList())
                .get(0);
        existing.setCategory(newCategory);

        final DishResponse response = getAdminAuthRequest()
                .when()
                .contentType(ContentType.JSON)
                .body(existing)
                .pathParam(ControllerConstant.ID, existing.getId())
                .put(DishController.GET_DISH_BY_ID)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract()
                .as(DishResponse.class);
        assertEquals(newCategory, response.getContent().getCategory(), "Update was not successful!");
        assertNotEquals(originalCategory, response.getContent().getCategory(), "Update was not successful!");
    }

    @Test
    public void givenExistingDish_whenPatchDishAsAdmin_thenReceivedDishWithTheSameIdAndUpdatedAndClearedAttributes() {
        final DishListResponse all = getAllAsAdmin();
        final Dish existing = all.getContents().get(0);
        final String newAttributeValue = "Ne edd meg leves";
        final DishRequest patchRequest = DishRequest.builder()
                .category(Category.soup)
                .name(newAttributeValue)
                .active(null)
                .recipe(0)
                .price(999)
                .build();

        final DishResponse response = getAdminAuthRequest()
                .when()
                .contentType(ContentType.JSON)
                .body(patchRequest)
                .pathParam(ControllerConstant.ID, existing.getId())
                .patch(DishController.GET_DISH_BY_ID)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract()
                .as(DishResponse.class);
        assertEquals(newAttributeValue, response.getContent().getName(), "Patch attribute update was not successful!");
        assertNull(response.getContent().getActive(), "Patch attribute clear was not successful!");
    }

    @Test
    public void givenDishId_whenDeleteDishByIdAsAdmin_thenDishWithTheSameIdWillBeDead() {
        final UUID dishId = getAllAsAdmin().getContents().get(0).getId();

        getAdminAuthRequest()
                .pathParam(ControllerConstant.ID, dishId)
                .when()
                .delete(DishController.GET_DISH_BY_ID)
                .prettyPeek()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        getAdminAuthRequest()
                .pathParam(ControllerConstant.ID, dishId)
                .when()
                .get(DishController.GET_DISH_BY_ID)
                .prettyPeek()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .contentType(ContentType.JSON);
    }

    private DishListResponse getAllAsAdmin() {
        final DishListResponse response = getAdminAuthRequest()
                .when()
                .queryParams(
                        Map.of(IntegrationTestHelper.QUERY_PARAM_PAGE, 0, IntegrationTestHelper.QUERY_PARAM_SIZE, 1000))
                .get(DishController.GET_ALL_ACTIVE_DISHES)
                .prettyPeek()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract()
                .as(DishListResponse.class);
        return response;
    }
}

package com.yokudlela.dailymenu.integration.test.v1.testcontainers;

import com.yokudlela.dailymenu.integration.IntegrationTestHelper;
import com.yokudlela.dailymenu.application.annotation.MethodLogging;
import com.yokudlela.dailymenu.business.model.menu.model.Menu;
import com.yokudlela.dailymenu.controller.ControllerConstant;
import com.yokudlela.dailymenu.controller.menu.MenuController;
import com.yokudlela.dailymenu.controller.menu.model.request.MenuRequest;
import com.yokudlela.dailymenu.controller.menu.model.response.MenuListResponse;
import com.yokudlela.dailymenu.controller.menu.model.response.MenuResponse;
import com.yokudlela.dailymenu.integration.BaseIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@MethodLogging
class MenuResourceIntegrationTest extends BaseIntegrationTest {

    @BeforeAll
    public static void setup() {
        RestAssured.basePath = ControllerConstant.API_V1 + ControllerConstant.MENU_PATH;
    }

    @Test
    public void testGetAllMenus() {
        given()
                .when()
                .get()
                .prettyPeek()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON);
    }

    @Test
    public void givenMenu_whenGetAllMenusAsAdmin_thenOk() {
        getAllAsAdmin();
    }

    @Test
    public void givenDay_whenGetMenuByDayAsAdmin_thenReceivedProperResultSet() {
        final LocalDate expectedDay = LocalDate.now();
        postMenuOnToDay(expectedDay);

        final MenuResponse response = getAdminAuthRequest()
                .queryParams(Map.of(
                        IntegrationTestHelper.PATH_PARAM_DAY, expectedDay.toString(),
                        IntegrationTestHelper.QUERY_PARAM_PAGE, 0, IntegrationTestHelper.QUERY_PARAM_SIZE, 1000))
                .when()
                .get(MenuController.GET_MENU_BY_DAY)
                .prettyPeek()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract()
                .as(MenuResponse.class);
        assertEquals(expectedDay, response.getContent().getDay(), "Wrong result!");
    }

    @Test
    public void givenDayInterval_whenGetMenuByDayAsAdmin_thenReceivedProperResultSet() {
        final LocalDate expectedDay = LocalDate.now().plusDays(1L);
        postMenuOnToDay(expectedDay);
        final LocalDate fromDay = expectedDay.minusDays(1L);
        final LocalDate toDay = expectedDay.plusDays(1L);

        final MenuListResponse response = getAdminAuthRequest()
                .queryParams(Map.of(
                        IntegrationTestHelper.PATH_PARAM_FROM_DAY, fromDay.toString(),
                        IntegrationTestHelper.PATH_PARAM_TO_DAY, toDay.toString(),
                        IntegrationTestHelper.QUERY_PARAM_PAGE, 0, IntegrationTestHelper.QUERY_PARAM_SIZE, 1000))
                .when()
                .get(MenuController.GET_MENU_BETWEEN_DAYS)
                .prettyPeek()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract()
                .as(MenuListResponse.class);
        final List<Menu> result = response.getContents();
        assertFalse(result.isEmpty(), "No result!");
        assertTrue(result.stream()
                        .allMatch(menu -> (menu.getDay().equals(fromDay) || menu.getDay().isAfter(fromDay))
                                && (menu.getDay().equals(toDay) || menu.getDay().isBefore(toDay))),
                "Wrong result!");
    }

    @Test
    public void givenMenuId_whenGetMenuByIdAsAdmin_thenReceivedMenuWithTheSameId() {
        final UUID expectedMenuId = getAllAsAdmin().getContents().get(0).getId();

        final MenuResponse response = getAdminAuthRequest()
                .pathParam(ControllerConstant.ID, expectedMenuId)
                .when()
                .get(MenuController.GET_MENU_BY_ID)
                .prettyPeek()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract()
                .as(MenuResponse.class);
        assertEquals(expectedMenuId, response.getContent().getId(), "Requested ID not found!");
    }

    @Test
    public void givenNewMenu_whenPostMenuAsAdmin_thenReceivedNewMenuWithTheSameAttributesAndGeneratedId() {
        postMenuOnToDay(LocalDate.now().plusDays(2L));
    }

    @Test
    public void givenExistingMenu_whenPutMenuAsAdmin_thenReceivedMenuWithTheSameIdAndUpdatedAttributes() {
        final LocalDate originalDay = LocalDate.now().plusDays(3L);
        postMenuOnToDay(originalDay);
        final MenuListResponse all = getAllAsAdmin();
        final Menu existing = all.getContents().get(0);
        final LocalDate newDay = originalDay.plusDays(7L);
        existing.setDay(newDay);

        final MenuResponse response = getAdminAuthRequest()
                .when()
                .contentType(ContentType.JSON)
                .body(existing)
                .pathParam(ControllerConstant.ID, existing.getId())
                .put(MenuController.GET_MENU_BY_ID)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract()
                .as(MenuResponse.class);
        assertEquals(newDay, response.getContent().getDay(), "Update was not successful!");
        assertNotEquals(originalDay, response.getContent().getDay(), "Update was not successful!");
    }

    @Test
    public void givenExistingMenu_whenPatchMenuAsAdmin_thenReceivedMenuWithTheSameIdAndUpdatedAndClearedAttributes() {
        postMenuOnToDay(LocalDate.now().plusDays(4L));
        final MenuListResponse all = getAllAsAdmin();
        final Menu existing = all.getContents().get(0);
        final LocalDate originalDay = existing.getDay();
        final LocalDate newDay = originalDay.plusDays(7L);
        existing.setDay(newDay);

        final MenuResponse response = getAdminAuthRequest()
                .when()
                .contentType(ContentType.JSON)
                .body(existing)
                .pathParam(ControllerConstant.ID, existing.getId())
                .patch(MenuController.GET_MENU_BY_ID)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract()
                .as(MenuResponse.class);
        assertEquals(newDay, response.getContent().getDay(), "Update was not successful!");
        assertNotEquals(originalDay, response.getContent().getDay(), "Update was not successful!");
    }

    @Disabled
    @Test
    public void givenMenuId_whenDeleteMenuByIdAsAdmin_thenMenuWithTheSameIdWillBeDead() {
        final LocalDate expectedDay = LocalDate.now().plusDays(5L);
        postMenuOnToDay(expectedDay);

        final MenuListResponse response = getAdminAuthRequest()
                .queryParams(Map.of(
                        IntegrationTestHelper.PATH_PARAM_DAY, expectedDay,
                        IntegrationTestHelper.QUERY_PARAM_PAGE, 0, IntegrationTestHelper.QUERY_PARAM_SIZE, 1000))
                .when()
                .get(MenuController.GET_MENU_BY_DAY)
                .prettyPeek()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract()
                .as(MenuListResponse.class);
        final List<Menu> result = response.getContents();
        final Menu found = result.stream().filter(menu -> expectedDay.equals(menu.getDay()))
                .findFirst().orElse(null);
        final UUID menuId = found.getId();

        getAdminAuthRequest()
                .pathParam(ControllerConstant.ID, menuId)
                .when()
                .delete(MenuController.GET_MENU_BY_ID)
                .prettyPeek()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        getAdminAuthRequest()
                .pathParam(ControllerConstant.ID, menuId)
                .when()
                .get(MenuController.GET_MENU_BY_ID)
                .prettyPeek()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .contentType(ContentType.JSON);
    }

    private MenuListResponse getAllAsAdmin() {
        final MenuListResponse response = getAdminAuthRequest()
                .when()
                .queryParams(
                        Map.of(IntegrationTestHelper.QUERY_PARAM_PAGE, 0, IntegrationTestHelper.QUERY_PARAM_SIZE, 1000))
                .get()
                .prettyPeek()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract()
                .as(MenuListResponse.class);
        return response;
    }

    private void postMenuOnToDay(LocalDate day) {
        final MenuRequest object = MenuRequest.builder()
                .day(day)
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

        final MenuResponse response = getAdminAuthRequest()
                .pathParam(ControllerConstant.ID, uuid)
                .when()
                .get(MenuController.GET_MENU_BY_ID)
                .prettyPeek()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract()
                .as(MenuResponse.class);
        assertEquals(uuid, response.getContent().getId(), "Requested ID not found!");
    }
}

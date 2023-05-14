package com.yokudlela.kitchen.junit

import com.yokudlela.kitchen.Constants.Companion.HTTP_LOCALHOST
import com.yokudlela.kitchen.Constants.Companion.KEYCLOAK_REALM
import com.yokudlela.kitchen.KitchenIntegrationBaseTest
import com.yokudlela.kitchen.controller.model.request.LocationRequest
import com.yokudlela.kitchen.controller.model.response.LocationResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.ws.rs.BadRequestException
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotFoundException
import kotlin.math.abs
import kotlin.random.Random


class LocationTest : KitchenIntegrationBaseTest() {

    @BeforeEach
    fun setup() {
        rootUri = "$HTTP_LOCALHOST:${port}/$LOCATION"
    }

    @Test
    fun addLocation_ok() {
        val location = aLocation()

        val response = sendPostAuth<LocationResponse>(rootUri, location, kitchenAdmin)

        assertEquals(201, response.statusCodeValue)
        assertEquals(location.name, response.body?.name)
        assertEquals(location.details, response.body?.details)
        assertNotNull(response.body?.id)
    }

    @Test
    fun addLocation_nok_badRequest() {
        val emptyName = LocationRequest("", "details")

        assertThrows<BadRequestException> {
            sendPostAuth<LocationResponse>(rootUri, emptyName, kitchenAdmin)
        }
    }

    @Test
    fun addLocation_nok_redirect() {
        val response = sendPostNoAuth<LocationResponse>(rootUri, aLocation())

        assertEquals(302, response.statusCodeValue)
        assertTrue(response.headers.location.toString().contains(KEYCLOAK_REALM))
    }

    @Test
    fun addLocation_nok_forbidden() {
        assertThrows<ForbiddenException> {
            sendPostAuth<LocationResponse>(rootUri, aLocation(), dailyMenuAdmin)
        }
    }

    @Test
    fun getLocation_ok() {
        val location = createLocation()

        val response = sendGetAuth<LocationResponse>("$rootUri/$locationId", kitchenAdmin)

        assertEquals(200, response.statusCodeValue)
        assertEquals(location.name, response.body?.name)
        assertEquals(location.details, response.body?.details)
        assertEquals(locationId, response.body?.id)
    }

    @Test
    fun getLocation_nok_notFound() {
        val randomId = abs(Random(10).nextInt())

        assertThrows<NotFoundException> {
            sendGetAuth<LocationResponse>("$rootUri/$randomId", kitchenAdmin)
        }
    }

    @Test
    fun getLocation_nok_redirect() {
        val randomId = abs(Random(10).nextInt())

        val response = sendGetNoAuth<LocationResponse>("$rootUri/$randomId")

        assertEquals(302, response.statusCodeValue)
        assertTrue(response.headers.location.toString().contains(KEYCLOAK_REALM))
    }

    @Test
    fun getAllLocations_ok() {
        val responseKa = sendGetAuth<List<LocationResponse>>("$rootUri/$ALL", kitchenAdmin)

        assertEquals(200, responseKa.statusCodeValue)

        val responseDma = sendGetAuth<List<LocationResponse>>("$rootUri/$ALL", dailyMenuAdmin)

        assertEquals(200, responseDma.statusCodeValue)
    }

    @Test
    fun getAllLocations_nok_redirect() {
        val response = sendGetNoAuth<List<LocationResponse>>("$rootUri/$ALL")

        assertEquals(302, response.statusCodeValue)
        assertTrue(response.headers.location.toString().contains(KEYCLOAK_REALM))
    }

    @Test
    fun putLocation_ok() {
        createLocation()

        val modified = LocationRequest("modified", "modified")
        val response = sendPutAuth<LocationResponse>("$rootUri/$locationId", modified, kitchenAdmin)

        assertEquals(200, response.statusCodeValue)
        assertEquals(modified.name, response.body?.name)
        assertEquals(modified.details, response.body?.details)
        assertEquals(locationId, response.body?.id)
    }

    @Test
    fun putLocation_nok_redirect() {
        val randomId = abs(Random(10).nextInt())

        val response = sendPutNoAuth<LocationResponse>("$rootUri/$randomId", aLocation())

        assertEquals(302, response.statusCodeValue)
        assertTrue(response.headers.location.toString().contains(KEYCLOAK_REALM))
    }

    @Test
    fun putLocation_nok_forbidden() {
        assertThrows<ForbiddenException> {
            sendPutAuth<LocationResponse>("$rootUri/$locationId", aLocation(), dailyMenuAdmin)
        }
    }

    @Test
    fun deleteLocation_ok() {
        createLocation()

        val response = sendDeleteAuth<Void>("$rootUri/$locationId", kitchenAdmin)

        assertEquals(204, response.statusCodeValue)
    }

    @Test
    fun deleteLocation_nok_redirect() {
        val randomId = abs(Random(10).nextInt())

        val response = sendDeleteNoAuth<LocationResponse>("$rootUri/$randomId")

        // * infix example
//        assertEquals(302, response.statusCodeValue)
        302 isEqualTo response.statusCodeValue
//        assertTrue(response.headers.location.toString().contains(KEYCLOAK_REALM))
        response.headers.location.toString() doesContain KEYCLOAK_REALM
    }

    @Test
    fun deleteLocation_nok_forbidden() {
        assertThrows<ForbiddenException> {
            sendDeleteAuth<LocationResponse>("$rootUri/$locationId", dailyMenuAdmin)
        }
    }
}
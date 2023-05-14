package com.yokudlela.kitchen.junit

import com.yokudlela.kitchen.Constants.Companion.HTTP_LOCALHOST
import com.yokudlela.kitchen.Constants.Companion.KEYCLOAK_REALM
import com.yokudlela.kitchen.KitchenIntegrationBaseTest
import com.yokudlela.kitchen.controller.model.request.OrderRequest
import com.yokudlela.kitchen.controller.model.response.OrderResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.ws.rs.BadRequestException
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotFoundException
import kotlin.math.abs
import kotlin.random.Random

class OrderTest : KitchenIntegrationBaseTest() {

    @BeforeEach
    fun setup() {
        rootUri = "$HTTP_LOCALHOST:${port}/$ORDER"
    }

    @Test
    fun addOrder_ok() {
        createLocation()
        val order = anOrder()

        val response = sendPostAuth<OrderResponse>(rootUri, order, dailyMenuAdmin)

        assertEquals(201, response.statusCodeValue)
        assertEquals(order.details, response.body?.details)
        assertEquals(locationId, response.body?.locationId)
        assertNotNull(response.body?.id)
    }

    @Test
    fun addOrder_nok_badRequest() {
        val emptyLocationId = OrderRequest(listOf(anOrderItem()), null, "details")

        assertThrows<BadRequestException> {
            sendPostAuth<OrderResponse>(rootUri, emptyLocationId, dailyMenuAdmin)
        }
    }

    @Test
    fun addOrder_nok_redirect() {
        val response = sendPostNoAuth<OrderResponse>(rootUri, anOrder())

        assertEquals(302, response.statusCodeValue)
        Assertions.assertTrue(response.headers.location.toString().contains(KEYCLOAK_REALM))
    }

    @Test
    fun addOrder_nok_forbidden() {
        assertThrows<ForbiddenException> {
            sendPostAuth<OrderResponse>(rootUri, anOrder(), kitchenAdmin)
        }
    }

    @Test
    fun getOrder_ok() {
        createLocation()
        val order = createOrder()

        val response = sendGetAuth<OrderResponse>("$rootUri/$orderId", kitchenAdmin)

        assertEquals(200, response.statusCodeValue)
        assertEquals(order.details, response.body?.details)
        assertEquals(order.locationId, response.body?.locationId)
        assertEquals(orderId, response.body?.id)
    }

    @Test
    fun getOrder_nok_notFound() {
        val randomId = Random(11).nextInt()

        assertThrows<NotFoundException> {
            sendGetAuth<OrderResponse>("$rootUri/$randomId", kitchenAdmin)
        }
    }

    @Test
    fun getOrder_nok_redirect() {
        val randomId = abs(Random(11).nextInt())

        val response = sendGetNoAuth<OrderResponse>("$rootUri/$randomId")

        assertEquals(302, response.statusCodeValue)
        Assertions.assertTrue(response.headers.location.toString().contains(KEYCLOAK_REALM))
    }

    @Test
    fun getAllOrders_ok() {
        val responseKa = sendGetAuth<List<OrderResponse>>("$rootUri/$ALL", kitchenAdmin)

        assertEquals(200, responseKa.statusCodeValue)

        val responseDma = sendGetAuth<List<OrderResponse>>("$rootUri/$ALL", dailyMenuAdmin)

        assertEquals(200, responseDma.statusCodeValue)
    }

    @Test
    fun getAllOrders_nok_redirect() {
        val response = sendGetNoAuth<List<OrderResponse>>("$rootUri/$ALL")

        assertEquals(302, response.statusCodeValue)
        Assertions.assertTrue(response.headers.location.toString().contains(KEYCLOAK_REALM))
    }

    @Test
    fun putOrder_ok() {
        createOrder()

        // * invoking PUT
        val modified = OrderRequest(listOf(anOrderItem()), locationId, "modified")
        val response = sendPutAuth<OrderResponse>("$rootUri/$MODIFY/$orderId", modified, dailyMenuAdmin)

        assertEquals(200, response.statusCodeValue)
        assertEquals(modified.details, response.body?.details)
        assertEquals(modified.locationId, response.body?.locationId)
        assertEquals(orderId, response.body?.id)
    }

    @Test
    fun putOrder_nok_redirect() {
        val randomId = abs(Random(11).nextInt())

        val response = sendPutNoAuth<OrderResponse>("$rootUri/$MODIFY/$randomId", anOrder())

        assertEquals(302, response.statusCodeValue)
        Assertions.assertTrue(response.headers.location.toString().contains(KEYCLOAK_REALM))
    }

    @Test
    fun putOrder_nok_forbidden() {
        assertThrows<ForbiddenException> {
            sendPutAuth<OrderResponse>("$rootUri/$MODIFY/$orderId", anOrder(), kitchenAdmin)
        }
    }

    @Test
    fun deleteOrder_ok() {
        createOrder()

        val response = sendDeleteAuth<Void>("$rootUri/$orderId", dailyMenuAdmin)

        assertEquals(204, response.statusCodeValue)
    }

    @Test
    fun deleteOrder_nok_redirect() {
        val randomId = abs(Random(11).nextInt())

        val response = sendDeleteNoAuth<OrderResponse>("$rootUri/$randomId")

        assertEquals(302, response.statusCodeValue)
        Assertions.assertTrue(response.headers.location.toString().contains(KEYCLOAK_REALM))
    }

    @Test
    fun deleteOrder_nok_forbidden() {
        assertThrows<ForbiddenException> {
            sendDeleteAuth<OrderResponse>("$rootUri/$orderId", kitchenAdmin)
        }
    }
}
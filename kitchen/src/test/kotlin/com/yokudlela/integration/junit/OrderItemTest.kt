package com.yokudlela.integration.junit

import com.yokudlela.integration.Constants.Companion.HTTP_LOCALHOST
import com.yokudlela.integration.Constants.Companion.KEYCLOAK_REALM
import com.yokudlela.integration.KitchenIntegrationBaseTest
import com.yokudlela.kitchen.controller.model.request.OrderItemRequest
import com.yokudlela.kitchen.controller.model.response.OrderItemResponse
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.ws.rs.BadRequestException
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotFoundException
import kotlin.math.abs
import kotlin.random.Random

class OrderItemTest : KitchenIntegrationBaseTest() {

    @BeforeEach
    fun setup() {
        rootUri = "$HTTP_LOCALHOST:${port}/$ORDER_ITEM"
    }

    @Test
    fun addOrderItem_ok() {
        createOrder()
        val orderItem = anOrderItem()

        val response = sendPostAuth<OrderItemResponse>(rootUri, orderItem, dailyMenuAdmin)

        assertEquals(201, response.statusCodeValue)
        assertEquals(orderItem.menuItemId, response.body?.menuItemId)
        assertEquals(orderItem.quantity, response.body?.quantity)
        assertEquals(orderItem.details, response.body?.details)
        assertEquals(orderItem.orderId, response.body?.orderId)
        assertThat(LocalDateTime.now()).isCloseTo(response.body?.timeOfRecord, within(5, ChronoUnit.SECONDS))
    }

    @Test
    fun addOrderItem_nok_badRequest() {
        val emptyMenuItemId = OrderItemRequest(orderId, null, 10, "details")

        assertThrows<BadRequestException> {
            sendPostAuth<OrderItemResponse>(rootUri, emptyMenuItemId, dailyMenuAdmin)
        }
    }

    @Test
    fun addOrderItem_nok_redirect() {
        val response = sendPostNoAuth<OrderItemResponse>(rootUri, anOrderItem())

        assertEquals(302, response.statusCodeValue)
        Assertions.assertTrue(response.headers.location.toString().contains(KEYCLOAK_REALM))
    }

    @Test
    fun addOrderItem_nok_forbidden() {
        assertThrows<ForbiddenException> {
            sendPostAuth<OrderItemResponse>(rootUri, anOrderItem(), kitchenAdmin)
        }
    }

    @Test
    fun getOrderItem_ok() {
        createOrder()
        val orderItem = createOrderItem()

        val response = sendGetAuth<OrderItemResponse>("$rootUri/$orderItemId", kitchenAdmin)

        assertEquals(200, response.statusCodeValue)
        assertEquals(orderItem.menuItemId, response.body?.menuItemId)
        assertEquals(orderItem.quantity, response.body?.quantity)
        assertEquals(orderItem.details, response.body?.details)
        assertEquals(orderItem.orderId, response.body?.orderId)
        assertEquals(orderItemId, response.body?.id)
    }

    @Test
    fun getOrderItem_nok_notFound() {
        val randomId = Random(12).nextInt()

        assertThrows<NotFoundException> {
            sendGetAuth<OrderItemResponse>("$rootUri/$randomId", kitchenAdmin)
        }
    }

    @Test
    fun getOrderItem_nok_redirect() {
        val randomId = abs(Random(12).nextInt())

        val response = sendGetNoAuth<OrderItemResponse>("$rootUri/$randomId")

        assertEquals(302, response.statusCodeValue)
        Assertions.assertTrue(response.headers.location.toString().contains(KEYCLOAK_REALM))
    }

    @Test
    fun getAllOrderItems_ok() {
        val responseKa = sendGetAuth<List<OrderItemResponse>>("$rootUri/$ALL", kitchenAdmin)

        assertEquals(200, responseKa.statusCodeValue)

        val responseDma = sendGetAuth<List<OrderItemResponse>>("$rootUri/$ALL", dailyMenuAdmin)

        assertEquals(200, responseDma.statusCodeValue)
    }

    @Test
    fun getAllOrderItems_nok_redirect() {
        val response = sendGetNoAuth<List<OrderItemResponse>>("$rootUri/$ALL")

        assertEquals(302, response.statusCodeValue)
        Assertions.assertTrue(response.headers.location.toString().contains(KEYCLOAK_REALM))
    }

    @Test
    fun putOrderItem_ok() {
        createOrderItem()

        val modified = OrderItemRequest(orderId, 1, 1, "modified")
        val response = sendPutAuth<OrderItemResponse>("$rootUri/$MODIFY/$orderItemId", modified, dailyMenuAdmin)

        assertEquals(200, response.statusCodeValue)
        assertEquals(modified.menuItemId, response.body?.menuItemId)
        assertEquals(modified.quantity, response.body?.quantity)
        assertEquals(modified.details, response.body?.details)
        assertEquals(modified.orderId, response.body?.orderId)
        assertThat(LocalDateTime.now()).isCloseTo(response.body?.timeOfModification, within(5, ChronoUnit.SECONDS))
        assertEquals(orderItemId, response.body?.id)
    }

    @Test
    fun putOrderItem_nok_redirect() {
        val randomId = abs(Random(12).nextInt())

        val response = sendPutNoAuth<OrderItemResponse>("$rootUri/$MODIFY/$randomId", anOrderItem())

        assertEquals(302, response.statusCodeValue)
        Assertions.assertTrue(response.headers.location.toString().contains(KEYCLOAK_REALM))
    }

    @Test
    fun putOrderItem_nok_forbidden() {
        assertThrows<ForbiddenException> {
            sendPutAuth<OrderItemResponse>("$rootUri/$MODIFY/$orderItemId", anOrderItem(), kitchenAdmin)
        }
    }

    @Test
    fun deleteOrderItem_ok() {
        createOrderItem()

        val response = sendDeleteAuth<Void>("$rootUri/$orderItemId", dailyMenuAdmin)

        assertEquals(204, response.statusCodeValue)
    }

    @Test
    fun deleteOrderItem_nok_redirect() {
        val randomId = abs(Random(12).nextInt())

        val response = sendDeleteNoAuth<OrderItemResponse>("$rootUri/$randomId")

        assertEquals(302, response.statusCodeValue)
        Assertions.assertTrue(response.headers.location.toString().contains(KEYCLOAK_REALM))
    }

    @Test
    fun deleteOrderItem_nok_forbidden() {
        assertThrows<ForbiddenException> {
            sendDeleteAuth<OrderItemResponse>("$rootUri/$orderItemId", kitchenAdmin)
        }
    }
}
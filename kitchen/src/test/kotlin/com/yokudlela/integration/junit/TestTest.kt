package com.yokudlela.integration.junit

import com.yokudlela.integration.Constants.Companion.HTTP_LOCALHOST
import com.yokudlela.integration.KitchenIntegrationBaseTest
import com.yokudlela.kitchen.controller.model.response.ResponseEntityId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class TestTest : KitchenIntegrationBaseTest() {

    companion object {

        private const val TEST = "test"
    }

    @BeforeEach
    fun setup() {
        rootUri = "$HTTP_LOCALHOST:${port}/$TEST"
    }

    @Test
    fun testTestEndpoint() {
        val response = restTemplate.getForEntity(rootUri, ResponseEntityId::class.java)
        Assertions.assertEquals(200, response.statusCodeValue)
        Assertions.assertEquals(10, response.body?.id)
    }
}
package com.yokudlela.kitchen.business.config

import com.yokudlela.kitchen.business.handler.OrderHandler
import com.yokudlela.kitchen.business.handler.OrderItemHandler
import com.yokudlela.kitchen.business.handler.RecipeHandler
import com.yokudlela.kitchen.business.model.OrderItem
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

@Configuration
class OrderItemConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun orderItem(
        orderItemHandler: OrderItemHandler,
        recipeHandler: RecipeHandler,
        orderHandler: OrderHandler,
    ): OrderItem {
        return OrderItem(orderItemHandler, recipeHandler, orderHandler)
    }

    @Lookup
    fun getOrderItem(): OrderItem {
        throw IllegalCallerException("Do not call me!")
    }
}
package com.yokudlela.kitchen.business.config

import com.yokudlela.kitchen.business.handler.LocationHandler
import com.yokudlela.kitchen.business.handler.OrderHandler
import com.yokudlela.kitchen.business.handler.RecipeHandler
import com.yokudlela.kitchen.business.model.Location
import com.yokudlela.kitchen.business.model.Order
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

@Configuration
class OrderConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun order(orderHandler: OrderHandler, recipeHandler: RecipeHandler, locationHandler: LocationHandler): Order {
        return Order(orderHandler, recipeHandler, locationHandler)
    }

    @Lookup
    fun getOrder(): Order {
        throw IllegalCallerException("Do not call me!")
    }
}
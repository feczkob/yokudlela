package com.yokudlela.kitchen.business.config

import com.yokudlela.kitchen.business.handler.LocationHandler
import com.yokudlela.kitchen.business.model.Location
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode

@Configuration
class LocationConfig {

    // TODO ask Zoli to explain
    @Bean
//    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun location(locationHandler: LocationHandler): Location {
        return Location(locationHandler)
    }

    @Lookup
    fun getLocation(): Location {
        throw IllegalCallerException("Do not call me!")
    }
}
package com.yokudlela.kitchen.application.config

import com.yokudlela.kitchen.controller.rest.request.RequestDataInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class ApplicationConfig {

    @Bean
    fun corsConfigurer(): WebMvcConfigurer? {
        return object : WebMvcConfigurer {
            @Autowired
            private lateinit var customInterceptor: RequestDataInterceptor
            override fun addInterceptors(registry: InterceptorRegistry) {
                registry.addInterceptor(customInterceptor)
            }
        }
    }
}
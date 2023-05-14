package com.yokudlela.kitchen.application.config

import com.yokudlela.kitchen.application.RequestScopedBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.web.context.WebApplicationContext


@Configuration
class RequestScopedBeanConfig {

    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    fun requestScopedBean() : RequestScopedBean {
        return RequestScopedBean()
    }
}
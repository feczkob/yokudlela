package com.yokudlela.integration.config

import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
@ComponentScan(basePackages = ["com.yokudlela.kitchen"])
class KitchenIntegrationTestConfig {

    @Bean
    fun restTemplate(): RestTemplate {
        val factory = HttpComponentsClientHttpRequestFactory()
        factory.httpClient = HttpClientBuilder.create().disableRedirectHandling().build()
        return RestTemplateBuilder()
            .errorHandler(RestTemplateResponseErrorHandler())
            .requestFactory { factory }
            .build()
    }
}
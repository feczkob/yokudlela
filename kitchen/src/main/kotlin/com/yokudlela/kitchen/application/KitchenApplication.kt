package com.yokudlela.kitchen.application

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@ComponentScan(basePackages = ["com.yokudlela.kitchen"])
@EntityScan(basePackages = ["com.yokudlela.kitchen.client.persistence.entity"])
@EnableJpaRepositories(basePackages = ["com.yokudlela.kitchen.client.persistence.repository"])
@EnableAutoConfiguration
@EnableCaching
@Configuration
class KitchenApplication

fun main(args: Array<String>) {
    runApplication<KitchenApplication>(*args)
}

package com.yokudlela.kitchen.application.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SwaggerConfig {

    @Value("\${server.port}")
    var port: Int = 0

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .servers(
                mutableListOf(
                    Server()
                        .url("http://localhost:${port}")
                        .description("local dev")
                )
            )
            .info(
                Info()
                    .title("Kitchen API")
                    .version("v1")
                    .description("Yokudlela project - Kitchen API")
                    .license(
                        License()
                            .name("4D Soft")
                            .url("https://www.4dsoft.hu")
                    )
                    .contact(
                        Contact()
                            .url("https://www.4dsoft.hu")
                            .name("Feczkó Botond")
                            .email("feczko_botond@4dsoft.hu")
                    )
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "openid", SecurityScheme()
                            .type(SecurityScheme.Type.OPENIDCONNECT)
                            .name("openid")
                            .description("Keycloak Yokudlela")
                            .openIdConnectUrl("https://yokudlela.drhealth.cloud/auth/realms/yokudlela/.well-known/openid-configuration")
                    )
            )
    }
}
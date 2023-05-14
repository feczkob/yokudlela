package com.yokudlela.kitchen.application.config

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.modelmapper.ModelMapper
import org.modelmapper.convention.MatchingStrategies
import org.openapitools.jackson.nullable.JsonNullableModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

@Configuration
class MapperConfig {

    /**
     * modelMapper bean definition
     * @return ModelMapper
     */
    @Bean
    fun modelMapper(): ModelMapper {
        val modelMapper = ModelMapper()
        modelMapper.configuration
            .setFieldMatchingEnabled(false)
            .setMatchingStrategy(MatchingStrategies.STRICT)
            .fieldAccessLevel = org.modelmapper.config.Configuration.AccessLevel.PRIVATE

//        val orderMap: PropertyMap<OrderRequest, Order> = object : PropertyMap<OrderRequest, Order>() {
//            override fun configure() {
//                map().orders = emptyList()
//            }
//        }
//        modelMapper.addMappings(orderMap)

        return modelMapper
    }

    /**
     * objectMapper bean definition
     * @return ObjectMapper
     */
    @Bean
    fun objectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper().registerKotlinModule()
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, false)

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT)

        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)

        objectMapper.findAndRegisterModules()


        objectMapper.propertyNamingStrategy = PropertyNamingStrategies.LOWER_CAMEL_CASE
        // * exclude objects from JSON which have null value
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        // * ...but allow nulls under a special type: JsonNullable to support PATCH with field clearing implementations
        // * if you want to clear a field by PATCH: you should send in JSON: "xyAttr": null
        // * otherwise you just omit to send field (if you don't want to change it)
        val jsonNullableModule = JsonNullableModule()
        objectMapper.registerModule(jsonNullableModule)
        val javaTimeModule = JavaTimeModule()
        javaTimeModule.addSerializer(LocalDate::class.java, LocalDateSerializer.INSTANCE)
        javaTimeModule.addDeserializer(LocalDate::class.java, LocalDateDeserializer.INSTANCE)
        objectMapper.registerModule(javaTimeModule)
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)

        return objectMapper
    }
}
package hu.soft4d.yokudlela3.application.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.quarkus.jackson.ObjectMapperCustomizer;
import org.openapitools.jackson.nullable.JsonNullableModule;

import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ObjectMapperConfiguration implements ObjectMapperCustomizer {

    public void customize(ObjectMapper mapper) {
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        // exclude objects from JSON which have null value
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // ...but allow nulls under a special type: JsonNullable to support PATCH with field clearing implementations
        // if you wanna clear a field by PATCH: you should send in JSON: "xyAttr": null
        // otherwise you just omit to send field (if you don't want to change it
        final JsonNullableModule jsonNullableModule = new JsonNullableModule();
        mapper.registerModule(jsonNullableModule);

        final JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
        javaTimeModule.addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE);
        mapper.registerModule(javaTimeModule);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
    }
}



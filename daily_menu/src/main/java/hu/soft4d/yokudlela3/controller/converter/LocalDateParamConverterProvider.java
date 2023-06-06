package hu.soft4d.yokudlela3.controller.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

@ApplicationScoped
@Provider
public class LocalDateParamConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        return rawType.equals(LocalDate.class)
                ? (ParamConverter<T>) new LocalDateConverter()
                : null;
    }
}

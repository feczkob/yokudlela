package hu.soft4d.yokudlela3.controller.converter;

import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ext.ParamConverter;

@ApplicationScoped
public class LocalDateConverter implements ParamConverter<LocalDate> {

    @Override
    public LocalDate fromString(String value) {
        return null == value
                ? null
                : LocalDate.parse(value);
    }

    @Override
    public String toString(LocalDate value) {
        return null == value
                ? null
                : value.toString();
    }
}

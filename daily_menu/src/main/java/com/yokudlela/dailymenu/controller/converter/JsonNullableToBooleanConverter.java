package com.yokudlela.dailymenu.controller.converter;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.openapitools.jackson.nullable.JsonNullable;

public class JsonNullableToBooleanConverter implements Converter<JsonNullable<Boolean>, Boolean> {

    @Override
    public Boolean convert(MappingContext<JsonNullable<Boolean>, Boolean> context) {
        JsonNullable<Boolean> source = context.getSource();
        return null == source || !source.isPresent()
                ? null
                : source.get();
    }
}


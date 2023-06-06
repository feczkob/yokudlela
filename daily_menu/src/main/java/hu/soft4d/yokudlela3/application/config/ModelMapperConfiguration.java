package hu.soft4d.yokudlela3.application.config;

import hu.soft4d.yokudlela3.controller.converter.JsonNullableToBooleanConverter;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class ModelMapperConfiguration {

    @Produces
    @ApplicationScoped
    public ModelMapper modelMapper() {
        final ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        modelMapper.addConverter(new JsonNullableToBooleanConverter()); // JsonNullable<Boolean> -> Boolean

        return modelMapper;
    }

}

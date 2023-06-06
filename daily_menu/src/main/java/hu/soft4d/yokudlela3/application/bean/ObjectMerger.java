package hu.soft4d.yokudlela3.application.bean;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ObjectMerger {

    @Inject
    ObjectMapper mapper;


    public ObjectMerger(ObjectMapper mapper) {
        this.mapper = mapper;
        this.mapper.setDefaultMergeable(true);
    }

    public <T> T updateValue(T valueToUpdate, Object overrides) throws JsonMappingException {
        return mapper.updateValue(valueToUpdate, overrides);
    }
}

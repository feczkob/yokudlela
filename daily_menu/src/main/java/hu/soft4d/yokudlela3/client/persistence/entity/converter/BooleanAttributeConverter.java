package hu.soft4d.yokudlela3.client.persistence.entity.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class BooleanAttributeConverter implements AttributeConverter<Boolean, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Boolean attribute) {
        return null != attribute && attribute
                ? 1
                : 0;
    }

    @Override
    public Boolean convertToEntityAttribute(Integer dbData) {
        return null != dbData && 0 != dbData;
    }
}

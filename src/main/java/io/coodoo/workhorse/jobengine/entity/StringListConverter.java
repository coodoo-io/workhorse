package io.coodoo.workhorse.jobengine.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(List<String> list) {
        if (list == null) {
            return null;
        }
        return String.join(DELIMITER, list);
    }

    @Override
    public List<String> convertToEntityAttribute(String joined) {
        if (joined == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(joined.split(DELIMITER)));
    }

}

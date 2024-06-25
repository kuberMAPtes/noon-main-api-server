package com.kube.noon.feed.domain.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.List;

@Converter
public class ListToStringConverter implements AttributeConverter<List<String>, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    // database 값으로 변경
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting list to JSON", e);
        }
    }

    // Entity 값으로 변경
    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting JSON to list", e);
        }
    }
}

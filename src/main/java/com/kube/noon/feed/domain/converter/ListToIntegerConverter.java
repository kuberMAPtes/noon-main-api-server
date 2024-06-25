package com.kube.noon.feed.domain.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.List;

@Converter
public class ListToIntegerConverter implements AttributeConverter<List<Integer>, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (IOException e) {
            throw new IllegalArgumentException("정수 리스트를 JSON으로 변환하는 중 오류 발생", e);
        }
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, new TypeReference<List<Integer>>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("JSON을 정수 리스트로 변환하는 중 오류 발생", e);
        }
    }
}

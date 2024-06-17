package com.kube.noon.member.library;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonNodeExample {
    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "{\"name\":\"John\", \"age\":30, \"address\":{\"city\":\"New York\",\"zip\":\"10001\"}, \"phones\":[\"123-456-7890\", \"987-654-3210\"]}";
        System.out.println("JSON String: " + jsonString);
        JsonNode rootNode = objectMapper.readTree(jsonString);

        // 다양한 메서드 사용 예제
        traverseJson(rootNode);
    }

    public static void traverseJson(JsonNode rootNode) {
        // 특정 필드 접근
        JsonNode nameNode = rootNode.path("name");
        String name = nameNode.asText();
        System.out.println("Name: " + name);

        // 숫자 필드 접근
        JsonNode ageNode = rootNode.path("age");
        int age = ageNode.asInt();
        System.out.println("Age: " + age);

        // 중첩된 객체 접근
        JsonNode addressNode = rootNode.path("address");
        String city = addressNode.path("city").asText();
        System.out.println("City: " + city);

        // 배열 접근
        JsonNode phonesNode = rootNode.path("phones");
        System.out.println("프리티"+phonesNode.toPrettyString());
        System.out.println("일반"+phonesNode.toString());
        if (phonesNode.isArray()) {
            for (JsonNode phoneNode : phonesNode) {
                System.out.println("Phone: " + phoneNode.asText());
            }
        }

        // 필드 존재 여부 확인
        boolean hasZip = addressNode.has("zip");
        System.out.println("Has zip: " + hasZip);

        // 필드 타입 확인
        boolean isAgeNumeric = ageNode.isNumber();
        System.out.println("Is age numeric: " + isAgeNumeric);

        // 노드 변환
        String jsonString = ageNode.toString();
        System.out.println("Age as JSON string: " + jsonString);

        // 부모 노드 가져오기
        JsonNode parentNode = ageNode.findParent("address");
        System.out.println("Parent of age node: " + parentNode);
    }
}

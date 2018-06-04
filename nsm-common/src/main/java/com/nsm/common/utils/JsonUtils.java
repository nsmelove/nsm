package com.nsm.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by nsm on 2018/6/2
 */
public class JsonUtils {
    private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    public static <T> String toJson(T t){
        String json = null;
        try {
            json = objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            logger.error("object to json error", e);
        }
        return json;
    }

    public static <T> T toObject(String json, Class<T> type) {
        T t = null;
        if(json == null) {
            return null;
        }
        try {
            t = objectMapper.readValue(json, type);
        } catch (IOException e) {
            logger.error("json to object error", e);
        }
        return t;
    }

    public static <T> List<T> toList(String json, Class<T> type) {
        List<T> list = null;
        if(json == null) {
            return null;
        }
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, type);
        try {
            list = objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            logger.error("json to list error", e);
        }
        return list;
    }

    public static <K,V> Map<K,V> toMap(String json, Class<K> keyType, Class<V> valueType) {
        Map<K,V> map = null;
        if(json == null) {
            return null;
        }
        JavaType javaType = objectMapper.getTypeFactory().constructMapType(Map.class,keyType,valueType);
        try {
            map = objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            logger.error("json to map error", e);
        }
        return map;
    }

    public static void main(String[] args) {
        System.out.println(toList("[\"a\",\"b\",\"c\"]", String.class));
    }
}

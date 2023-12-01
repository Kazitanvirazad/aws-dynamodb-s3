package com.serverless.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SerializeUtil<T> {
    private static Logger LOGGER = LogManager.getLogger(SerializeUtil.class);

    /**
     * DeSerializes the JSON string passed in the method argument to the respective class type
     * passed in the method argument
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return POJO deserialized from the json string or null if deserialization fails
     */
    public static <T> T deSerialize(String json, Class clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        T t = null;
        try {
            t = (T) objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage());
        }
        return t;
    }


    /**
     * Serializes the POJO and returns JSON string
     *
     * @param value
     * @param <T>
     * @return Serialized JSON String of the POJO passed in the method argument
     */
    public static <T> String serialize(T value) {
        ObjectMapper objectMapper = new ObjectMapper();
        String res = null;
        try {
            res = objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage());
        }
        return res;
    }

}

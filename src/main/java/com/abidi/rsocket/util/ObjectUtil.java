package com.abidi.rsocket.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;

import java.io.IOException;

public class ObjectUtil {

    public static Payload toPayLoad (Object o) {

        ObjectMapper objectMapper = new ObjectMapper();
        byte [] bytes;

        try {
            bytes = objectMapper.writeValueAsBytes(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return DefaultPayload.create(bytes);
    }

    public static <T> T toObject(Payload payload, Class<T> type) {
        ObjectMapper objectMapper = new ObjectMapper();

        byte[] bytes = payload.getData().array();
        try {

            return objectMapper.readValue(bytes, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

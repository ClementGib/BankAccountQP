package com.cdx.bas.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class CustomObjectMapper {
    private static final ObjectMapper INSTANCE = new ObjectMapper();


    static {
        // Add all custom configurations here
        INSTANCE.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private CustomObjectMapper() {}

    public static ObjectMapper getCustomObjectMapper() {
        return INSTANCE;
    }
}

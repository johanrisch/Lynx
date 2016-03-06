package com.jrisch.lynx_android_jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jrisch.lynx.ILynxObjectMapper;
import java.io.IOException;
import java.io.InputStream;

public class LynxObjectMapper implements ILynxObjectMapper {

    private final ObjectMapper mapper;

    public LynxObjectMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public LynxObjectMapper(){

        this.mapper = new ObjectMapper();
    }

    @Override
    public <V> byte[] serialize(V object) {
        try {
            return mapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <V> V deserialize(byte[] data, Class<V> cls) {
        try {
            return mapper.readValue(data, cls);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <V> V deserialize(InputStream inputStream, Class<V> cls) {
        try {
            return mapper.readValue(inputStream, cls);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

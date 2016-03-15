package com.jrisch.lynx_android;

import com.google.gson.Gson;
import com.jrisch.lynx.ILynxObjectMapper;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class LynxObjectMapper implements ILynxObjectMapper {

    private Gson gson;

    LynxObjectMapper(Gson gson) {
        this.gson = gson;
    }

    public LynxObjectMapper(){
        this.gson = new Gson();
    }

    @Override
    public <V> byte[] serialize(V object) {
        return gson.toJson(object).getBytes(Charset.defaultCharset());
    }

    @Override
    public <V> V deserialize(byte[] data, Class<V> cls) {
        return gson.fromJson(new String(data, Charset.defaultCharset()), cls);
    }

    @Override
    public <V> V deserialize(InputStream inputStream, Class<V> cls) {
        return gson.fromJson(new InputStreamReader(inputStream), cls);
    }
}

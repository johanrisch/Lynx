package com.jrisch.lynx;

import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TODO - add description
 */
public class LynxObjectMapperTest {

    private Gson gson;
    private com.jrisch.lynx.util.LynxObjectMapper lynxObjectMapper;

    @Before
    public void setUp() throws Exception {
        gson = new Gson();
        lynxObjectMapper = new com.jrisch.lynx.util.LynxObjectMapper(gson);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test() throws Exception {
        HashMap<String,String> testMap = new HashMap<>();
        testMap.put("foo","bar");
        testMap.put("bar","foo");
        testMap.put("baz","baz");

        byte[] data = lynxObjectMapper.serialize(testMap);

        HashMap<String,String> byteArrData = lynxObjectMapper.deserialize(data,new HashMap<String,String>().getClass());
        HashMap<String,String> byteStreamData = lynxObjectMapper.deserialize(new ByteArrayInputStream(data),new HashMap<String,String>().getClass());

        assertTrue("foo not present in byteArr", byteArrData.get("foo").equalsIgnoreCase("bar"));
        assertTrue("bar not present in byteArr", byteArrData.get("bar").equalsIgnoreCase("foo"));
        assertTrue("baz not present in byteArr", byteArrData.get("baz").equalsIgnoreCase("baz"));

        assertTrue("foo not present in byteStreamData", byteStreamData.get("foo").equalsIgnoreCase("bar"));
        assertTrue("bar not present in byteStreamData", byteStreamData.get("bar").equalsIgnoreCase("foo"));
        assertTrue("baz not present in byteStreamData", byteStreamData.get("baz").equalsIgnoreCase("baz"));

    }

}
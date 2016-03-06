package com.jrisch.lynx;

import com.google.gson.Gson;
import com.jrisch.lynx.util.LynxObjectMapper;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * TODO - add description
 */
public class LynxTest {

    @Mock
    IStorageInfo storageInfo;

    @Mock
    ILynxCipherHandler cipherHandler;

    @Mock
    IFileHandler fileHandler;

    @Mock
    ILynxObjectMapper mapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        File externalFile = new File("external/");
        this.mapper = new LynxObjectMapper(new Gson());
        when(storageInfo.getStorageRoot()).thenReturn(externalFile);
        when(storageInfo.isStorageWritable()).thenReturn(true);
        when(storageInfo.getStorageRoot()).thenReturn(new File("external/"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThatExceptionIsThrownWhenNoStorageInfoIsSupplied() {
        Lynx.create(null, String.class, String.class).build();
    }

    @Test
    public void testThatDefaultValuesWork() {
        LynxDiskStorage<String, String> storage = Lynx.create(storageInfo, String.class, String.class).withMapper(mapper).build();

        testFunction(storage);
    }

    @Test
    public void testThatCihperHandlerIsSet() {
        Lynx<String, String> builder = Lynx.create(storageInfo, String.class, String.class).withMapper(mapper)
                                           .withCipherHandler(cipherHandler);
        LynxDiskStorage<String, String> storage = builder.build();

        assertSame("Wrong cipherhandler", cipherHandler, builder.cipherHandler);
    }

    @Test
    public void testThatFileHandlerIsSet() {
        Lynx<String, String> builder = Lynx.create(storageInfo, String.class, String.class).withMapper(mapper)
                                           .withFileHandler(fileHandler);
        LynxDiskStorage<String, String> storage = builder.build();

        assertSame("Wrong filehandler", fileHandler, builder.fileHandler);
    }

    @Test
    public void testThatMapperIsSet() {
        Lynx<String, String> builder = Lynx.create(storageInfo, String.class, String.class).withMapper(mapper);
        LynxDiskStorage<String, String> storage = builder.build();

        assertSame("Wrong mapper", mapper, builder.mapper);
    }

    @Test
    public void testThatNamedCreatedDifferentStorages() {
        Lynx<String, String> builder = Lynx.create(storageInfo, String.class, String.class).withMapper(mapper).named("foo");
        LynxDiskStorage<String, String> storage = builder.build();
        LynxDiskStorage<String, String> storage2 = builder.named("bar").build();
        storage.set("foo", "bar");
        storage2.set("bar", "foo");

        assertFalse("storage has bar cached", storage.isCached("bar"));
        assertFalse("storage2 has foo cached", storage2.isCached("foo"));
    }

    private void testFunction(LynxDiskStorage<String, String> storage) {
        storage.clear();
        assertFalse("Foo is cached", storage.isCached("foo"));
        storage.set("foo", "bar");
        assertTrue("Foo is not cached", storage.isCached("foo"));
        assertTrue("Value for Foo is not bar", storage.get("foo").equalsIgnoreCase("bar"));
        storage.remove("foo");
        assertFalse("Foo is cached", storage.isCached("foo"));
        storage.clear();
    }
}
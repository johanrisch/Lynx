package com.jrisch.lynx;

import com.google.gson.Gson;
import java.io.File;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * TODO - add description
 */
public class LynxDiskStorageTest {

    IFileHandler IFileHandler;

    com.jrisch.lynx.util.LynxObjectMapper lynxObjectMapper;

    @Mock
    IStorageInfo storageInfo;

    private LynxDiskStorage<String, String> lynxDiskStorage;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        File externalFile = new File("external/");
        lynxObjectMapper = new com.jrisch.lynx.util.LynxObjectMapper(new Gson());
        when(storageInfo.getStorageRoot()).thenReturn(externalFile);
        when(storageInfo.isStorageWritable()).thenReturn(true);
        IFileHandler = new FileHandler(storageInfo);
        this.lynxDiskStorage = new LynxDiskStorage<>(lynxObjectMapper, IFileHandler, String.class, String.class, "testName");
    }

    @Test
    public void testGet() throws Exception {
        //Arrange
        lynxDiskStorage.set("foo", "bar");

        //Act
        String ret = lynxDiskStorage.get("foo");

        //Assert
        assertTrue("Ret is not bar", ret.equalsIgnoreCase("bar"));
    }

    @Test
    public void testSet() throws Exception {
        //Arrange
        String str = "bar";
        lynxDiskStorage.set("foo", str);

        //Act
        String result = lynxDiskStorage.get("foo");

        //Assert
        assertTrue("foo is not bar", result.equalsIgnoreCase("bar"));
    }

    @Test
    public void testGetAll() throws Exception {
        //Arrange
        lynxDiskStorage.set("foo", "bar");
        lynxDiskStorage.set("foo2", "bar2");

        //Act
        List<String> ret = lynxDiskStorage.getAll();

        //Assert
        assertTrue("Ret is not bar", ret.get(0).equalsIgnoreCase("bar"));
        assertTrue("Ret is not bar2", ret.get(1).equalsIgnoreCase("bar2"));
    }

    @Test
    public void testIsCached() throws Exception {
        //Arrange
        lynxDiskStorage.set("foo", "bar");

        //Act
        boolean fooCached = lynxDiskStorage.isCached("foo");
        boolean bazCached = lynxDiskStorage.isCached("baz");

        //Assert
        assertTrue("foo is not cached", fooCached);
        assertFalse("baz is cached", bazCached);
    }

    @Test
    public void testRemove() throws Exception {
        //Arrange
        lynxDiskStorage.set("foo", "bar");

        //Act
        lynxDiskStorage.remove("foo");

        //Assert
        assertFalse("Foo is cached", lynxDiskStorage.isCached("foo"));
    }

    @Test
    public void testClear() throws Exception {
        //Arrange
        lynxDiskStorage.set("foo", "bar");
        lynxDiskStorage.set("bar", "baz");

        //Act
        lynxDiskStorage.clear();

        //Assert
        assertFalse("foo is cached", lynxDiskStorage.isCached("foo"));
        assertFalse("bar is cached", lynxDiskStorage.isCached("bar"));
    }

    @Test
    public void testThatEmptyStringNotOverridesTheFolder() throws Exception {
        //Arrange
        lynxDiskStorage.set("foo", "bar");
        lynxDiskStorage.set("bar", "baz");

        //Act
        lynxDiskStorage.set("","hej");

        //Assert
        assertFalse("Root file is not a folder", new File(lynxDiskStorage.getSubPath("testName")).isDirectory());
    }

    @After
    public void tearDown(){
        lynxDiskStorage.clear();
    }
}
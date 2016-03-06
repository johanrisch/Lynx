package com.jrisch.lynx;

import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * TODO - add description
 */
public class FileHandlerTest {

    @Mock
    IStorageInfo storageInfo;

    @Mock
    File mockFile;
    private File externalFile;
    private FileHandler externalFileHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        externalFile = new File("external/");
        when(storageInfo.getStorageRoot()).thenReturn(externalFile);
        when(storageInfo.isStorageWritable()).thenReturn(true);
        externalFileHandler = new FileHandler(storageInfo);
    }

    @After
    public void tearDown() throws Exception {
        externalFileHandler.deleteDirectory(null);
    }

    @Test
    public void testConstructor() {
        assertTrue("Using externalFile files dir - ", externalFileHandler.rootFile.getAbsolutePath().equalsIgnoreCase(externalFile.getAbsolutePath()));
    }

    @Test
    public void testSetupFileSystem() throws Exception {
        File file = externalFileHandler.setupFileSystem();
        assertTrue("Root folder is incorrect", file.getAbsolutePath().equalsIgnoreCase(externalFile.getAbsolutePath()));

        disableStorage();
        file = externalFileHandler.setupFileSystem();
        assertNull("Root folder returned when storage is non-writable", file);
    }

    @Test
    public void testCreateFile() throws Exception {
        externalFileHandler.createFile("testCreateFile", "testCreateFile.txt", "This is a test".getBytes());
        File f = new File(externalFile.getAbsolutePath() + "/testCreateFile/testCreateFile.txt");
        assertTrue("File not found " + f.getAbsolutePath(), f.exists());

        disableStorage();
        assertNull("Storage is unavailable", externalFileHandler.createFile("testCreateFile", "testCreateFile.txt", "This is a test".getBytes()));
    }

    @Test
    public void testCreateFile1() throws Exception {
        externalFileHandler.createFile("testCreateFile", "testCreateFile2.txt", "This is a test2");
        File f = new File(externalFile.getAbsolutePath() + "/testCreateFile/testCreateFile2.txt");
        assertTrue("File not found " + f.getAbsolutePath(), f.exists());
    }

    @Test
    public void testReadFile() throws Exception {
        externalFileHandler.createFile("testCreateFile", "testCreateFile.txt", "This is a test".getBytes());
        externalFileHandler.createFile("testCreateFile", "testCreateFile2.txt", "This is a test2");

        byte[] result = externalFileHandler.readFile("testCreateFile", "testCreateFile.txt");
        assertArrayEquals("Read content not equal to 'This is a test' ", result, "This is a test".getBytes());

        result = externalFileHandler.readFile("testCreateFile", "testCreateFile2.txt");
        assertArrayEquals("Read content not equal to 'This is a test' ", result, "This is a test2".getBytes());

        disableStorage();
        assertNull("File is read when external storage is non-writable", externalFileHandler.readFile("testCreateFile", "testCreateFile.txt"));
    }

    @Test
    public void testCreateDirectory() throws Exception {
        externalFileHandler.createDirectory("testDirectory");
        File file = new File(externalFile.getAbsolutePath() + "/testDirectory");
        assertTrue("File does not exist", file.exists());
        assertTrue("File is not a directory", file.isDirectory());

        disableStorage();
        assertFalse("Storage is unavailable", externalFileHandler.createDirectory("foo"));
    }

    @Test
    public void testHasEnoughSpaceFor() throws Exception {
        when(mockFile.getFreeSpace()).thenReturn(100L);
        when(mockFile.getTotalSpace()).thenReturn(200L);
        when(storageInfo.getStorageRoot()).thenReturn(mockFile);
        IFileHandler fh = new FileHandler(storageInfo);
        assertFalse("Should not have space for this", fh.hasEnoughSpaceFor(101));
        assertTrue("Should have space for this", fh.hasEnoughSpaceFor(10));
    }

    @Test
    public void testSpaceLeft() throws Exception {
        when(mockFile.getFreeSpace()).thenReturn(100L);
        when(mockFile.getTotalSpace()).thenReturn(200L);
        when(storageInfo.getStorageRoot()).thenReturn(mockFile);
        IFileHandler fh = new FileHandler(storageInfo);
        assertEquals("Should have 100 bytes free", fh.spaceInfo()[0], 100L);
        assertEquals("Should have 200 bytes total", fh.spaceInfo()[1], 200L);
    }

    @Test
    public void testGetFiles() throws Exception {
        File f1 = externalFileHandler.createFile("test", "test1", "test");
        File f2 = externalFileHandler.createFile("test", "test2", "test");
        File f3 = externalFileHandler.createFile("test", "test3", "test");
        File[] result = externalFileHandler.getFiles("test");
        assertEquals("Wrong amount of files", result.length, 3);

        assertTrue("File1 wrong path", result[0].getAbsolutePath().equalsIgnoreCase(f1.getAbsolutePath()));
        assertTrue("File2 wrong path", result[1].getAbsolutePath().equalsIgnoreCase(f2.getAbsolutePath()));
        assertTrue("File3 wrong path", result[2].getAbsolutePath().equalsIgnoreCase(f3.getAbsolutePath()));

        disableStorage();
        assertNull("Storage is unavailable", externalFileHandler.getFiles("test"));
    }

    @Test
    public void testDeleteDirectory() throws Exception {
        externalFileHandler.deleteDirectory(null);
        assertFalse("Directory exists", externalFileHandler.rootFile.exists());

        externalFileHandler.createFile("testDeleteFile", "fileToDelete.txt", "this will be deleted");
        File file = new File(externalFile.getAbsolutePath() + "/testDeleteFile/");
        assertTrue("File was never created", file.exists());

        externalFileHandler.deleteDirectory("testDeleteFile");
        assertFalse("File exists", file.exists());
    }

    @Test
    public void testDeleteFile() throws Exception {
        externalFileHandler.createFile("testDeleteFile", "fileToDelete.txt", "this will be deleted");
        File file = new File(externalFile.getAbsolutePath() + "/testDeleteFile/fileToDelete.txt");
        assertTrue("File was never created", file.exists());

        externalFileHandler.deleteFile("testDeleteFile", "fileToDelete.txt");
        assertFalse("File exists", file.exists());
    }

    @Test(expected = IFileHandler.ExternalStorageNotWriteableException.class)
    public void testDeleteFileWhenRootFileIsNull() throws Exception {
        externalFileHandler.createFile("testDeleteFile", "fileToDelete.txt", "this will be deleted");
        File file = new File(externalFile.getAbsolutePath() + "/testDeleteFile/fileToDelete.txt");
        assertTrue("File was never created", file.exists());
        when(storageInfo.isStorageWritable()).thenReturn(false);
        externalFileHandler.deleteFile("testDeleteFile", "fileToDelete.txt");
    }

    @Test
    public void testDeleteFile1() throws Exception {
        externalFileHandler.createFile("testDeleteFile", "fileToDelete.txt", "this will be deleted");
        File file = new File(externalFile.getAbsolutePath() + "/testDeleteFile/fileToDelete.txt");
        assertTrue("File was never created", file.exists());

        externalFileHandler.deleteFile(externalFile.getAbsolutePath() + "/testDeleteFile/fileToDelete.txt");
        assertFalse("File exists", file.exists());
    }

    @Test(expected = IFileHandler.ExternalStorageNotWriteableException.class)
    public void testDeleteFile1WhenRootIsNull() throws Exception {
        externalFileHandler.createFile("testDeleteFile", "fileToDelete.txt", "this will be deleted");
        File file = new File(externalFile.getAbsolutePath() + "/testDeleteFile/fileToDelete.txt");
        assertTrue("File was never created", file.exists());
        when(storageInfo.isStorageWritable()).thenReturn(false);
        externalFileHandler.deleteFile(externalFile.getAbsolutePath() + "/testDeleteFile/fileToDelete.txt");
    }

    @Test
    public void testIsFileExist() throws Exception {

        externalFileHandler.createFile("testDeleteFile", "fileToDelete.txt", "this will be deleted");
        assertTrue("Created file does not exist", externalFileHandler.isFileExist("testDeleteFile", "fileToDelete.txt"));
        assertFalse("Bogus file exists", externalFileHandler.isFileExist("testDeleteFifle", "filfeToDelete.txt"));

        disableStorage();
        assertFalse("Storage is disabled", externalFileHandler.isFileExist("testDeleteFile", "fileToDelete.txt"));
    }

    @Test
    public void testCombinePath() throws Exception {
        String path = "this/is/a/path/";
        String result = externalFileHandler.combinePath(path.split("/"));
        assertTrue("Path is not correct", result.equalsIgnoreCase(path));

        path = "/";
        result = externalFileHandler.combinePath(path.split("/"));
        assertTrue("Test single slash " + result, result.equalsIgnoreCase(""));
    }

    private void disableStorage() {when(storageInfo.isStorageWritable()).thenReturn(false);}
}
package org.matsim.viz.files.file;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.matsim.viz.error.InternalException;
import org.matsim.viz.files.entities.FileEntry;
import org.matsim.viz.files.entities.PendingFileTransfer;
import org.matsim.viz.files.entities.Project;
import org.matsim.viz.files.util.TestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class S3RepositoryTest {

    private static final String uploadDirectory = "./tmpTestUploads";
    private static final String testBucketName = "test-bucket";

    @AfterClass
    public static void tearDownFixture() throws IOException {
        Path start = Paths.get(uploadDirectory);
        TestUtils.removeFileTree(start);
    }

    @After
    public void tearDown() {
        TestUtils.removeAllEntities();
    }

    @Test
    public void constructor_object() {
        S3Repository repository = new S3Repository(new FileDAO(TestUtils.getPersistenceUnit()),
                mock(AmazonS3Client.class), testBucketName, uploadDirectory);
        assertNotNull(repository);
    }

    @Test
    public void addFile_success() throws InterruptedException {

        FileDAO dao = new FileDAO(TestUtils.getPersistenceUnit());
        Project project = TestUtils.persistProjectWithCreator("project-name");
        final String filename = "s3test.txt";
        final String contentType = "plain/text";
        final InputStream inputStream = mock(InputStream.class);
        final AmazonS3Client s3 = mock(AmazonS3Client.class);
        S3Repository repo = createRepo(s3);
        FileUpload upload = new FileUpload(filename, contentType, inputStream, new String[]{});

        FileEntry fileEntry = repo.addFile(upload);
        project.addFileEntry(fileEntry);
        project = TestUtils.getProjectDAO().persist(project);

        assertEquals(1, project.getFiles().size());
        fileEntry = project.getFiles().iterator().next();

        assertEquals(filename, fileEntry.getUserFileName());
        assertEquals(contentType, fileEntry.getContentType());
        assertEquals(FileEntry.StorageType.Local, fileEntry.getStorageType());
        Thread.sleep(3500); // this smells but is the easy fix here

        verify(s3).putObject(eq(testBucketName), eq(fileEntry.getPersistedFileName()), any(File.class));
        FileEntry transferred = dao.findFileEntryById(fileEntry.getId());
        assertEquals(FileEntry.StorageType.S3, transferred.getStorageType());
        assertNull(transferred.getPendingFileTransfer());
    }

    @Test
    public void addFile_s3throws_leaveFileLocal() throws InterruptedException {

        Project project = TestUtils.persistProjectWithCreator("project-name");
        final String filename = "s3test.txt";
        final String contentType = "plain/text";
        final InputStream inputStream = mock(InputStream.class);
        final AmazonS3Client s3 = mock(AmazonS3Client.class);
        when(s3.putObject(anyString(), anyString(), any(File.class))).thenThrow(new AmazonServiceException("error"));
        S3Repository repo = createRepo(s3);
        FileUpload upload = new FileUpload(filename, contentType, inputStream, new String[0]);

        FileEntry fileEntry = repo.addFile(upload);
        project.addFileEntry(fileEntry);
        project = TestUtils.getProjectDAO().persist(project);

        assertEquals(1, project.getFiles().size());
        fileEntry = project.getFiles().iterator().next();

        assertEquals(filename, fileEntry.getUserFileName());
        assertEquals(contentType, fileEntry.getContentType());
        assertEquals(FileEntry.StorageType.Local, fileEntry.getStorageType());
        Thread.sleep(3500); // this smells but is the easy fix here

        verify(s3).putObject(eq(testBucketName), eq(fileEntry.getPersistedFileName()), any(File.class));
        FileEntry transferred = new FileDAO(TestUtils.getPersistenceUnit()).findFileEntryById(fileEntry.getId());
        assertEquals(FileEntry.StorageType.Local, transferred.getStorageType());
        assertNotNull(transferred.getPendingFileTransfer());
        TestCase.assertEquals(PendingFileTransfer.Status.Failed, transferred.getPendingFileTransfer().getStatus());
    }

    @Test
    public void addFiles_success() throws InterruptedException {

        Project project = TestUtils.persistProjectWithCreator("project-name");
        final String filename = "s3test.txt";
        final String secondFilename = "otherfile.txt";
        final String contentType = "plain/text";
        final InputStream inputStream = mock(InputStream.class);
        final AmazonS3Client s3 = mock(AmazonS3Client.class);
        S3Repository repo = createRepo(s3);
        List<FileUpload> uploads = new ArrayList<>(2);
        uploads.add(new FileUpload(filename, contentType, inputStream, new String[0]));
        uploads.add(new FileUpload(secondFilename, contentType, inputStream, new String[0]));

        List<FileEntry> fileEntries = repo.addFiles(uploads);
        project.addFileEntries(fileEntries);
        project = TestUtils.getProjectDAO().persist(project);

        assertEquals(2, project.getFiles().size());
        FileEntry fileEntry = project.getFiles().iterator().next();

        assertEquals(contentType, fileEntry.getContentType());
        assertEquals(FileEntry.StorageType.Local, fileEntry.getStorageType());
        Thread.sleep(3500); // this smells but is the easy fix here

        verify(s3, times(2)).putObject(eq(testBucketName), anyString(), any(File.class));
        FileEntry transferred = new FileDAO(TestUtils.getPersistenceUnit()).findFileEntryById(fileEntry.getId());
        assertEquals(FileEntry.StorageType.S3, transferred.getStorageType());
        assertNull(transferred.getPendingFileTransfer());
    }

    @Test
    public void getFileStream_success() {

        S3ObjectInputStream inputStream = mock(S3ObjectInputStream.class);
        S3Object s3Object = mock(S3Object.class);
        when(s3Object.getObjectContent()).thenReturn(inputStream);
        AmazonS3Client s3 = mock(AmazonS3Client.class);
        when(s3.getObject(anyString(), anyString())).thenReturn(s3Object);
        S3Repository repo = createRepo(s3);

        FileEntry entry = new FileEntry();
        entry.setPersistedFileName("any-name");
        entry.setStorageType(FileEntry.StorageType.S3);

        InputStream result = repo.getFileStream(entry);

        assertNotNull(result);
        assertEquals(inputStream, result);
        verify(s3).getObject(anyString(), eq(entry.getPersistedFileName()));
    }

    @Test
    public void getFileStream_storageTypeIsLocal_readFromDisk() throws IOException {

        AmazonS3Client s3 = mock(AmazonS3Client.class);
        S3Repository repo = createRepo(s3);

        FileEntry entry = new FileEntry();
        entry.setPersistedFileName("test-file.txt");
        entry.setStorageType(FileEntry.StorageType.Local);

        String content = "This is a test file and should be removed after unit testing";
        TestUtils.writeTextFile(Paths.get(uploadDirectory, entry.getPersistedFileName()), content);

        //noinspection EmptyTryBlock
        try (InputStream result = repo.getFileStream(entry)) {
            // nothing. Just make sure stream is closed
        }

        verify(s3, never()).getObject(anyString(), anyString());
    }

    @Test(expected = InternalException.class)
    public void getFileStream_errorWhileConnecting_exception() {

        AmazonS3Client s3 = mock(AmazonS3Client.class);
        when(s3.getObject(anyString(), anyString())).thenThrow(new SdkClientException("some error"));
        S3Repository repo = createRepo(s3);
        FileEntry entry = new FileEntry();
        entry.setPersistedFileName("any-name");
        entry.setStorageType(FileEntry.StorageType.S3);

        repo.getFileStream(entry);

        fail("Failing S3-Connection should cause exception");
    }

    @Test
    public void removeFile_success() {

        AmazonS3Client s3 = mock(AmazonS3Client.class);
        S3Repository repo = createRepo(s3);

        FileEntry entry = new FileEntry();
        entry.setPersistedFileName("some-name.txt");
        entry.setStorageType(FileEntry.StorageType.S3);

        repo.removeFile(entry);

        verify(s3, times(1)).deleteObject(anyString(), eq(entry.getPersistedFileName()));
    }

    @Test
    public void removeFile_storageTypeIsLocal_removeFromDisk() {

        AmazonS3Client s3 = mock(AmazonS3Client.class);
        S3Repository repo = createRepo(s3);

        FileEntry entry = new FileEntry();
        entry.setPersistedFileName("test-file-to-be-removed.txt");
        entry.setStorageType(FileEntry.StorageType.Local);

        String content = "This is a test file and should be removed after unit testing";
        TestUtils.writeTextFile(Paths.get(uploadDirectory, entry.getPersistedFileName()), content);

        repo.removeFile(entry);

        verify(s3, never()).deleteObject(anyString(), anyString());
        assertFalse(Files.exists(Paths.get(uploadDirectory, entry.getPersistedFileName())));
    }

    @Test(expected = InternalException.class)
    public void removeFile_errorWhileConnecting_exception() {

        AmazonS3Client s3 = mock(AmazonS3Client.class);
        doThrow(new AmazonServiceException("some error")).when(s3).deleteObject(anyString(), anyString());
        S3Repository repo = createRepo(s3);
        FileEntry entry = new FileEntry();
        entry.setPersistedFileName("any-name");
        entry.setStorageType(FileEntry.StorageType.S3);

        repo.removeFile(entry);

        fail("Failing S3-Connection should cause exception");
    }

    private S3Repository createRepo(AmazonS3Client client) {
        return new S3Repository(new FileDAO(TestUtils.getPersistenceUnit()),
                client, testBucketName, uploadDirectory);
    }
}

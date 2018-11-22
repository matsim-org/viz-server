package org.matsim.viz.files.file;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.*;
import org.matsim.viz.files.entities.FileEntry;
import org.matsim.viz.files.util.TestUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LocalRepositoryTest {

    private static final String uploadDirectory = "./testUploads";


    private LocalRepository testObject;

    @BeforeClass
    public static void setUpFixture() {

        TestUtils.loadTestConfig();
    }

    @AfterClass
    public static void tearDownFixture() throws IOException {
        Path start = Paths.get(uploadDirectory);
        TestUtils.removeFileTree(start);
    }

    @Before
    public void setUp() {
        testObject = new LocalRepository(uploadDirectory);
    }

    @After
    public void tearDown() {
        TestUtils.removeAllEntities();
    }

    @Test
    public void constructor_object() {

        LocalRepository repository = new LocalRepository(uploadDirectory);
        assertNotNull(repository);
    }

    @Test
    public void addFile_success() throws IOException {

        final String filename = "file.name";
        final String contentType = "content-type";
        final String tagsString = "first-tag.second-tag";
        InputStream inputStream = mock(InputStream.class);
        FileUpload upload = new FileUpload(filename, contentType, inputStream, tagsString.split("\\."));

        FileEntry entry = testObject.addFile(upload);

        //noinspection ResultOfMethodCallIgnored
        verify(inputStream).read(any());

        assertEquals(filename, entry.getUserFileName());
        assertEquals(FilenameUtils.getExtension(filename), FilenameUtils.getExtension(entry.getPersistedFileName()));
        assertEquals(contentType, entry.getContentType());
        assertEquals(0, entry.getSizeInBytes());
        assertEquals(2, entry.getTags().size());
        assertTrue(StringUtils.isNotBlank(entry.getTagSummary()));
    }

    @Test
    public void addFiles_success() {

        InputStream inputStream = mock(InputStream.class);
        List<FileUpload> uploads = new ArrayList<>();
        uploads.add(new FileUpload("name", "type", inputStream, new String[]{"some-tag"}));
        uploads.add(new FileUpload("other", "type", inputStream, new String[]{"some-tag"}));

        List<FileEntry> entries = testObject.addFiles(uploads);

        assertEquals(uploads.size(), entries.size());
    }

    @Test
    public void getFileStream() throws IOException {

        FileEntry entry = new FileEntry();
        entry.setPersistedFileName("test.txt");
        Path directory = Paths.get(uploadDirectory);

        //write a test file
        String testText = "this is a test file and should be removed after unit testing";

        try (BufferedWriter writer = Files.newBufferedWriter(directory.resolve(entry.getPersistedFileName()))) {
            writer.write(testText, 0, testText.length());
        } catch (IOException e) {
            fail("could not write test file");
        }

        try (InputStream result = testObject.getFileStream(entry)) {
            assertNotNull(result);
            try (StringWriter writer = new StringWriter()) {
                IOUtils.copy(result, writer, Charset.defaultCharset());
                String text = writer.toString();
                assertEquals(testText, text);
            }
        }
    }
}

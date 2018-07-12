package org.matsim.webvis.files.file;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.junit.*;
import org.matsim.webvis.files.agent.UserDAO;
import org.matsim.webvis.files.config.AppConfiguration;
import org.matsim.webvis.files.entities.FileEntry;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.project.ProjectDAO;
import org.matsim.webvis.files.util.TestUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DiskProjectRepositoryTest {

    private Project project;
    private ProjectDAO projectDAO = new ProjectDAO();
    private UserDAO userDAO = new UserDAO();
    private DiskProjectRepository testObject;

    @BeforeClass
    public static void setUpFixture() {
        TestUtils.loadTestConfig();
    }

    @AfterClass
    public static void tearDownFixture() throws IOException {
        Path start = Paths.get(AppConfiguration.getInstance().getUploadFilePath());
        TestUtils.removeFileTree(start);
    }

    @Before
    public void setUp() {
        User user = userDAO.persist(new User());
        Project toPersist = new Project();
        toPersist.setCreator(user);
        project = projectDAO.persist(toPersist);
        testObject = new DiskProjectRepository(project);
    }

    @After
    public void tearDown() {
        TestUtils.removeAllEntities();
    }

    @Test
    public void constructor_object() {

        DiskProjectRepository repository = new DiskProjectRepository(project);
        assertNotNull(repository);
    }

    @Test
    public void addFile_success() throws IOException {

        final String filename = "file.name";
        final String contentType = "content-type";
        InputStream inputStream = mock(InputStream.class);
        FileUpload upload = new FileUpload(filename, contentType, inputStream);

        FileEntry entry = testObject.addFile(upload);

        //noinspection ResultOfMethodCallIgnored
        verify(inputStream).read(any());

        assertEquals(filename, entry.getUserFileName());
        assertEquals(FilenameUtils.getExtension(filename), FilenameUtils.getExtension(entry.getPersistedFileName()));
        assertEquals(contentType, entry.getContentType());
        assertEquals(0, entry.getSizeInBytes());
    }

    @Test
    public void addFiles_success() {

        InputStream inputStream = mock(InputStream.class);
        List<FileUpload> uploads = new ArrayList<>();
        uploads.add(new FileUpload("name", "type", inputStream));
        uploads.add(new FileUpload("other", "type", inputStream));

        List<FileEntry> entries = testObject.addFiles(uploads);

        assertEquals(uploads.size(), entries.size());
    }

    @Test
    public void getFileStream() throws IOException {

        FileEntry entry = new FileEntry();
        entry.setPersistedFileName("test.txt");
        Path directory = testObject.getProjectDirectory();

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
                IOUtils.copy(result, writer);
                String text = writer.toString();
                assertEquals(testText, text);
            }
        }
    }
}

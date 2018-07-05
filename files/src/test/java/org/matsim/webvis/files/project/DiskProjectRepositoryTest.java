package org.matsim.webvis.files.project;

import org.junit.*;
import org.matsim.webvis.files.agent.UserDAO;
import org.matsim.webvis.files.config.Configuration;
import org.matsim.webvis.files.entities.FileEntry;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.util.TestUtils;
import spark.utils.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static junit.framework.TestCase.*;

public class DiskProjectRepositoryTest {

    private Project project;
    private ProjectDAO projectDAO = new ProjectDAO();
    private UserDAO userDAO = new UserDAO();
    private DiskProjectRepository testObject;

    @BeforeClass
    public static void setUpFixture() throws UnsupportedEncodingException, FileNotFoundException {
        TestUtils.loadConfig();
    }

    @AfterClass
    public static void tearDownFixture() throws IOException {
        Path start = Paths.get(Configuration.getInstance().getUploadedFilePath());
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
        userDAO.removeAllUser();
        projectDAO.removeAllProjects();
    }

    @Test
    public void constructor_object() {

        DiskProjectRepository repository = new DiskProjectRepository(project);
        assertNotNull(repository);
    }

    /* @Test
     public void addFile() throws Exception {

         final String filename = "filename.file";
         final String contentType = "content-type";
         final long size = 1L;
         FileItem item = TestUtils.mockFileItem(filename, contentType, size);
         List<FileItem> items = new ArrayList<>();
         items.add(item);

         List<FileEntry> entries = testObject.addFiles(items);

         verify(item).write(any());
         assertEquals(1, entries.size());
         FileEntry entry = entries.get(0);
         assertEquals(filename, entry.getUserFileName());
         assertEquals(contentType, entry.getContentType());
         assertEquals(size, entry.getSizeInBytes());
         String diskFilename = entry.getPersistedFileName().split("\\.")[0];
         UUID uuid = UUID.fromString(diskFilename);
         assertNotNull(uuid);
     }
 */
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

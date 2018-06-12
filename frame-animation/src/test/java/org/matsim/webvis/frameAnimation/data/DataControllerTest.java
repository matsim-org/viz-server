package org.matsim.webvis.frameAnimation.data;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.frameAnimation.communication.ServiceCommunication;
import org.matsim.webvis.frameAnimation.config.Configuration;
import org.matsim.webvis.frameAnimation.utils.TestUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class DataControllerTest {

    @BeforeClass
    public static void setUp() {
        TestUtils.loadConfig();
        ServiceCommunication.initialize(true);
    }

    /**
     * removes files like https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileVisitor.html
     *
     * @param start root of the file tree. Will be removed as well.
     * @throws IOException if something goes wrong
     */
    public static void removeFileTree(Path start) throws IOException {
        if (!Files.exists(start)) {
            return;
        }
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                if (e == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    throw e;
                }
            }
        });
    }

    @After
    public void tearDown() throws IOException {
        removeFileTree(Paths.get(Configuration.getInstance().getTmpFilePath()));
    }

    @Test
    public void test() {

        DataController controller = DataController.Instance;

        controller.fetchVisualizationData();
    }
}

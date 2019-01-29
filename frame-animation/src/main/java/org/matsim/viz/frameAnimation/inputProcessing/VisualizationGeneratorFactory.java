package org.matsim.viz.frameAnimation.inputProcessing;

import lombok.RequiredArgsConstructor;
import org.matsim.viz.filesApi.FilesApi;
import org.matsim.viz.filesApi.Visualization;

import javax.persistence.EntityManagerFactory;
import java.nio.file.Path;

/**
 * basically for unit testing VisualizationFetcher
 */
@RequiredArgsConstructor
public class VisualizationGeneratorFactory {

    private final FilesApi filesAPI;
    private final EntityManagerFactory emFactory;
    private final Path tmpFolder;

    VisualizationGenerator create(Visualization visualization) {
        return new VisualizationGenerator(filesAPI, tmpFolder, visualization, emFactory);
    }
}

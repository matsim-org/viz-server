package org.matsim.viz.postprocessing.emissions;

import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.matsim.viz.filesApi.FilesApi;
import org.matsim.viz.filesApi.Visualization;

import java.nio.file.Path;

@RequiredArgsConstructor
class VisualizationGeneratorFactory {

    private final SessionFactory sessionFactory;
    private final FilesApi filesApi;
    private final Path tmpFiles;

    VisualizationGenerator createGenerator(Visualization visualization) {
        return new VisualizationGenerator(sessionFactory, filesApi, tmpFiles, visualization);
    }

}

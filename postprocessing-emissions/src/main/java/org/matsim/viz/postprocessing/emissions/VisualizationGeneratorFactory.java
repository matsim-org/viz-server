package org.matsim.viz.postprocessing.emissions;

import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.matsim.viz.filesApi.FilesApi;

import java.nio.file.Path;

@RequiredArgsConstructor
public class VisualizationGeneratorFactory {

    private final SessionFactory sessionFactory;
    private final FilesApi filesApi;
    private final Path tmpFiles;

    public VisualizationGenerator createGenerator() {
        return new VisualizationGenerator(sessionFactory, filesApi, tmpFiles);
    }

}

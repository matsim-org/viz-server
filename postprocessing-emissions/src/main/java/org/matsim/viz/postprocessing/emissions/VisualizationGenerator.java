package org.matsim.viz.postprocessing.emissions;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.hibernate.SessionFactory;
import org.matsim.viz.filesApi.FilesApi;

import java.nio.file.Path;

@Log
@RequiredArgsConstructor
public class VisualizationGenerator {

    private final SessionFactory sessionFactory;
    private final FilesApi filesApi;
    private final Path tmpFiles;

    public void generate() {
        log.warning("Here should be some generating logic!");
    }
}

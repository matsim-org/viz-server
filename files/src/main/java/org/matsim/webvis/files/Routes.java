package org.matsim.webvis.files;

import org.matsim.webvis.files.communication.AuthenticationHandler;
import org.matsim.webvis.files.config.Configuration;
import org.matsim.webvis.files.file.FileDeleteRequestHandler;
import org.matsim.webvis.files.file.FileDownloadRequestHandler;
import org.matsim.webvis.files.file.FileUploadRequestHandler;
import org.matsim.webvis.files.project.CreateProjectRequestHandler;
import org.matsim.webvis.files.project.ProjectRequestHandler;
import org.matsim.webvis.files.visualization.CreateVisualizationRequestHandler;
import org.matsim.webvis.files.visualization.VisualizationRequestHandler;
import org.matsim.webvis.files.visualization.VisualizationsRequestHandler;

import java.net.URI;
import java.nio.file.Paths;

import static spark.Spark.*;

class Routes {

    private final static String FILE = "file/";
    private final static String FILE_UPLOAD = FILE + "upload/";
    private final static String PROJECT = "project/";
    private final static String VISUALIZATION = PROJECT + "visualization/";
    private final static String VISUALIZATIONS = PROJECT + "visualizations/";

    static void initialize() {

        put(PROJECT, new CreateProjectRequestHandler());
        post(PROJECT, new ProjectRequestHandler());
        post(FILE_UPLOAD, new FileUploadRequestHandler());
        post(FILE, new FileDownloadRequestHandler());
        delete(FILE, new FileDeleteRequestHandler());
        put(VISUALIZATION, new CreateVisualizationRequestHandler());
        post(VISUALIZATION, new VisualizationRequestHandler());
        post(VISUALIZATIONS, new VisualizationsRequestHandler());

    }
}

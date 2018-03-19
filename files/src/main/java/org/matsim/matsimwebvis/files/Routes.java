package org.matsim.matsimwebvis.files;

import org.matsim.matsimwebvis.files.file.UploadFileRequestHandler;
import org.matsim.matsimwebvis.files.project.CreateProjectRequestHandler;

import static spark.Spark.post;
import static spark.Spark.put;

public class Routes {

    private final static String FILE = "file/";
    private final static String PROJECT = "project/";

    static void initialize() {

        put(PROJECT, new CreateProjectRequestHandler());
        post(FILE, new UploadFileRequestHandler());
    }
}

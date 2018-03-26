package org.matsim.webvis.files;

import org.matsim.webvis.files.file.FileUploadRequestHandler;
import org.matsim.webvis.files.project.CreateProjectRequestHandler;

import static spark.Spark.post;
import static spark.Spark.put;

public class Routes {

    private final static String FILE = "file/";
    private final static String PROJECT = "project/";

    static void initialize() {

        put(PROJECT, new CreateProjectRequestHandler());
        post(FILE, new FileUploadRequestHandler());
    }
}

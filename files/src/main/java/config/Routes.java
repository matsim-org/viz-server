package config;

import project.CreateProjectRequestHandler;

import static spark.Spark.put;

public class Routes {

    private final static String FILE = "file/";
    private final static String PROJECT = "project/";

    static void initialize() {

        put(PROJECT, new CreateProjectRequestHandler());

    }
}

package org.matsim.webvis.files;

import org.matsim.webvis.files.communication.AuthenticationHandler;
import org.matsim.webvis.files.config.Configuration;
import org.matsim.webvis.files.file.FileUploadRequestHandler;
import org.matsim.webvis.files.project.CreateProjectRequestHandler;
import org.matsim.webvis.files.project.ProjectRequestHandler;

import java.net.URI;
import java.nio.file.Paths;

import static spark.Spark.*;

public class Routes {

    private final static String FILE = "file/";
    private final static String PROJECT = "project/";

    static void initialize() {

        AuthenticationHandler authHandler = AuthenticationHandler.builder()
                .setIntrospectionEndpoint(URI.create(Configuration.getInstance().getIntrospectionEndpoint()))
                .setRelyingPartyId(Configuration.getInstance().getRelyingPartyId())
                .setRelyingPartySecret(Configuration.getInstance().getRelyingPartySecret())
                .setTrustStore(Paths.get(Configuration.getInstance().getTlsTrustStore()))
                .setTrustStorePassword(Configuration.getInstance().getTlsTrustStorePassword().toCharArray())
                .build();

        before((request, response) -> {

            String origin = request.headers("Origin");
            response.header("Access-Control-Allow-Origin", (origin != null) ? origin : "*");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        });

        before((request, response) -> {
            if (request.requestMethod() != "OPTIONS") {
                authHandler.handle(request, response);
            }
        });

        options("/*", (request, response) -> {

           /* String origin = request.headers("Origin");
            response.header("Access-Control-Allow-Origin", (origin != null) ? origin : "*");
            response.header("Access-Control-Allow-Headers", "Content-Type,  Authorization");
            response.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
*/
            return "OK";
        });



        put(PROJECT, new CreateProjectRequestHandler());
        post(PROJECT, new ProjectRequestHandler());
        post(FILE, new FileUploadRequestHandler());
    }
}

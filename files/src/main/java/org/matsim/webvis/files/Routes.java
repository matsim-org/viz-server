package org.matsim.webvis.files;

import org.matsim.webvis.files.communication.AuthenticationHandler;
import org.matsim.webvis.files.config.Configuration;
import org.matsim.webvis.files.file.FileUploadRequestHandler;
import org.matsim.webvis.files.project.CreateProjectRequestHandler;

import java.net.URI;
import java.nio.file.Paths;

import static spark.Spark.*;

public class Routes {

    private final static String FILE = "file/";
    private final static String PROJECT = "project/";

    static void initialize() {

        before(AuthenticationHandler.builder()
                       .setIntrospectionEndpoint(URI.create(Configuration.getInstance().getIntrospectionEndpoint()))
                       .setRelyingPartyid(Configuration.getInstance().getRelyingPartyId())
                       .setRelyingPartySecret(Configuration.getInstance().getRelyingPartySecret())
                       .setTrustStore(Paths.get(Configuration.getInstance().getTlsTrustStore()))
                       .setTrustStorePassword(Configuration.getInstance().getTlsTrustStorePassword().toCharArray())
                       .build());

        put(PROJECT, new CreateProjectRequestHandler());
        post(FILE, new FileUploadRequestHandler());
    }
}

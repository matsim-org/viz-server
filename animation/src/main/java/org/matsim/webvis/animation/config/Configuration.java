package org.matsim.webvis.animation.config;

import com.google.gson.Gson;
import lombok.Getter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;

@Getter
public class Configuration {

    private static Configuration instance = new Configuration();

    private int port = 3002;
    private URI introspectionEndpoint;

    private String tlsKeyStore = "";
    private String tlsKeyStorePassword = "";
    private String tlsTrustStore = "";
    private String tlsTrustStorePassword = "";

    private String relyingPartyId = "relyingPartyId";
    private String relyingPartySecret = "secret";

    private Configuration() {
    }

    public static Configuration getInstance() {
        return instance;
    }

    public static void loadConfigFile(String filePath) throws FileNotFoundException {

        FileReader reader = new FileReader(filePath);
        instance = new Gson().fromJson(reader, Configuration.class);
    }
}

package org.matsim.webvis.files.config;

import com.google.gson.Gson;
import lombok.Getter;

import java.io.FileNotFoundException;
import java.io.FileReader;

@Getter
public class Configuration {

    private static Configuration instance = new Configuration();

    private boolean debug = false;
    private int port = 3001;
    private String uploadedFilePath = "./files";
    private String tmpFilePath = "./tmp";
    private String introspectionEndpoint = "https://localhost:3000/introspect/";
    private String tlsTrustStore = "./trustStore.jks";
    private String tlsTrustStorePassword = "nopassword";
    private String tlsKeyStore = "./keystore.jks";
    private String tlsKeyStorePassword = "nopassword";
    private String relyingPartyId = "relyingPartyId";
    private String relyingPartySecret = "secret";

    private Configuration() {
    }

    public static Configuration getInstance() {
        return instance;
    }

    public static void loadConfigFile(String filePath, boolean debug) throws FileNotFoundException {

        FileReader reader = new FileReader(filePath);
        instance = new Gson().fromJson(reader, Configuration.class);
        instance.debug = debug;
    }
}

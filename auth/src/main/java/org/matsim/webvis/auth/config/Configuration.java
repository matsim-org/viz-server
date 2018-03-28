package org.matsim.webvis.auth.config;

import com.google.gson.Gson;
import lombok.Getter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Configuration {

    private static Configuration instance = new Configuration();

    private boolean debug = false;

    private int port = 3000;
    private List<ConfigUser> users = new ArrayList<>();
    private List<ConfigClient> clients = new ArrayList<>();
    private List<ConfigRelyingParty> protectedResources = new ArrayList<>();

    private String tlsKeyStore = "";
    private String tlsKeyStorePassword = "";
    private String tlsKeyAlias = "";

    private String tokenSigningKeyStore = "";
    private String tokenSigningKeyStorePassword = "";
    private String tokenSigningKeyAlias = "";

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

    /**
     * This method is for unit testing. It assumes a keystore is present as a resource. It sets the tokenSigningKeyStore
     * parameter to the absolute path of the keyStore resource. also it sets the debug mode.
     *
     * @param filePath path to the test configuration file
     * @throws UnsupportedEncodingException if the system does not support UTF-8
     * @throws FileNotFoundException        if no configuration file was found
     */
    public static void loadTestConfig(String filePath) throws UnsupportedEncodingException, FileNotFoundException {

        loadConfigFile(filePath, true);
        instance.tokenSigningKeyStore = URLDecoder.decode(
                Configuration.class.getClassLoader().getResource(instance.tokenSigningKeyStore).getFile(), "UTF-8");
    }

    public static void clearConfig() {
        instance = new Configuration();
    }
}

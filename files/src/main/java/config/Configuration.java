package config;

import com.google.gson.Gson;
import lombok.Getter;

import java.io.FileNotFoundException;
import java.io.FileReader;

@Getter
public class Configuration {

    private static Configuration instance = new Configuration();

    private boolean debug = false;
    private int port = 3001;

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

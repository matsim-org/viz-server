package config;

import com.google.gson.Gson;
import data.entities.Client;
import lombok.Getter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Configuration {

    private static Configuration instance = new Configuration();
    private int port = 3000;
    private List<ConfigUser> users = new ArrayList<>();
    private List<Client> clients = new ArrayList<>();

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

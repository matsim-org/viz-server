import client.ClientDAO;
import config.CommandlineArgs;
import config.Configuration;
import org.junit.After;
import org.junit.Test;
import user.UserDAO;

import java.net.URLDecoder;

import static org.junit.Assert.assertEquals;

public class ServerTest {

    @After
    public void tearDown() {
        new ClientDAO().removeAllClients();
        new UserDAO().removeAllUsers();
    }

    @Test
    public void loadConfigFile_noConfigFile_defaultConfig() throws Exception {

        CommandlineArgs args = new CommandlineArgs();
        args.configFile = null;

        Server.loadConfigFile(args);

        assertEquals(3000, Configuration.getInstance().getPort());
        assertEquals(0, Configuration.getInstance().getUsers().size());
        assertEquals(0, Configuration.getInstance().getClients().size());
    }

    @Test
    public void loadConfigFile_configFile_loadTestConfig() throws Exception {

        CommandlineArgs args = new CommandlineArgs();
        args.configFile = URLDecoder.decode(this.getClass().getResource("test-config.json").getFile(), "UTF-8");

        Server.loadConfigFile(args);

        assertEquals(3000, Configuration.getInstance().getPort());
        assertEquals(1, Configuration.getInstance().getUsers().size());
        assertEquals(1, Configuration.getInstance().getClients().size());
    }
}

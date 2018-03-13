package token;

import config.Configuration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.TestUtils;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.interfaces.RSAPublicKey;

import static org.junit.Assert.assertNotNull;

public class RSAKeyProviderTest {

    private RSAKeyProvider testObject;

    @BeforeClass
    public static void setUpFixture() throws UnsupportedEncodingException, FileNotFoundException {

        String configpath = TestUtils.getTestConfigPath();
        Configuration.loadConfigFile(configpath);
    }

    @Before
    public void setUp() throws Exception {

        String keystorepath = URLDecoder.decode(this.getClass().getResource("/keystore.jks").getFile(), "UTF-8");
        testObject = new RSAKeyProvider(keystorepath);
    }

    @Test
    public void getPublicKey_dontKnow() throws UnsupportedEncodingException {


        RSAPublicKey key = testObject.getPublicKey();
        assertNotNull(key);
    }
}

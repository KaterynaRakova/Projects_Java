package currencyClientApp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {
    public static String readApiKey(String key) {
        try (InputStream inputStream = Utils.class
                .getClassLoader()
                .getResourceAsStream("configuration.properties")) {

            if (inputStream == null) {
                throw new RuntimeException("configuration.properties not found.");
            }

            Properties props = new Properties();
            props.load(inputStream);

            return props.getProperty(key);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load API key", e);
        }
    }

}

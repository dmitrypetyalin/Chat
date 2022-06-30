package ru.gb.chat.props;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 21.06.2022 13:08
 *
 * @author PetSoft
 */
public class PropertyReader {
    private static PropertyReader instance;
    private String host;
    private int port;
    private long authTimeout;

    private PropertyReader() {
        getPropValues();
    }

    public static PropertyReader getInstance() {
        if (instance == null) {
            instance = new PropertyReader();
        }
        return instance;
    }

    public void getPropValues() {
        var propFileName = "application.properties";
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName)) {
            var properties = new Properties();
            properties.load(inputStream);
            host = properties.getProperty("host");
            port = Integer.parseInt(properties.getProperty("port"));
            authTimeout = Long.parseLong(properties.getProperty("auth.timeout"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public long getAuthTimeout() {
        return authTimeout;
    }
}

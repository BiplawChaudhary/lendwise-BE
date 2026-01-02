package com.lendwise.iam.utils.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author Bishranta Bhattarai
 * @version v1.0
 * @since 2024-06-20
 **/

public class ApplicationPropertyHotLoader {

    private static final Logger log = LoggerFactory.getLogger(ApplicationPropertyHotLoader.class);
    private static final String PROPERTIES_FILE = "/application.properties";

    private ApplicationPropertyHotLoader() {
    }

    public static String getMessageKey(String key) {
        Properties prop = new Properties();
        try (InputStream inputStream = ApplicationPropertyHotLoader.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream == null) {
                log.error("application.properties file not found on classpath");
                return "";
            }
            prop.load(inputStream);
            return prop.getProperty(key);
        } catch (IOException e) {
            log.error("IOException occurred while reading application.properties: {}", e.getMessage());
            return "";
        } catch (Exception e) {
            log.error("Exception occurred while reading application.properties: {}", e.getMessage());
            return "";
        }
    }

    public static String getMessage(String key, Object... arguments) {
        String pattern = getMessageKey(key);
        if (pattern.isEmpty()) {
            log.warn("Property pattern not found for key: {}", key);
            return "";
        }
        return MessageFormat.format(pattern, arguments);
    }


    public Map<String, String> loadALlApplicationProperties() {
        Properties prop = new Properties();
        Map<String, String> propertiesMap = null;
        try (InputStream inputStream = ApplicationPropertyHotLoader.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream == null) {
                log.error("application.properties file not found on classpath");
            }
            prop.load(inputStream);
            propertiesMap = prop.entrySet().stream().collect(Collectors.toMap(e -> (String) e.getKey(), e -> (String) e.getValue()));
        } catch (IOException e) {
            log.error("IOException occurred while reading application.properties: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Exception occurred while reading application.properties: {}", e.getMessage());
        }
        return propertiesMap;
    }


}

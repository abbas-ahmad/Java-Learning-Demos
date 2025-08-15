package org.example.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Utility class for validating URLs.
 */
public class UrlValidator {
    /**
     * Validates the input URL. Throws IllegalArgumentException if invalid.
     * @param url the URL string to validate
     */
    public static void validate(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL must not be null or empty");
        }
        try {
            URL parsed = new URL(url);
            String protocol = parsed.getProtocol();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                throw new IllegalArgumentException("Only HTTP and HTTPS URLs are supported");
            }
            String host = parsed.getHost();
            if (host == null || host.trim().isEmpty()) {
                throw new IllegalArgumentException("URL must have a valid host");
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }
}

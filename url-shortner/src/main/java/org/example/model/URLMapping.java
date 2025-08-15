package org.example.model;

import java.time.LocalDateTime;

/**
 * Represents a mapping between a short code and a long URL, including creation timestamp.
 * Used by the URL shortener service to store and retrieve URL associations.
 */
public class URLMapping {
    /**
     * The unique short code representing the long URL.
     */
    private final String shortCode;
    /**
     * The original long URL to be shortened.
     */
    private final String longUrl;
    /**
     * The timestamp when the mapping was created.
     */
    private final LocalDateTime createdAt;

    /**
     * Constructs a new URLMapping.
     * @param shortCode the unique short code
     * @param longUrl the original long URL
     * @param createdAt the creation timestamp
     */
    public URLMapping(String shortCode, String longUrl, LocalDateTime createdAt) {
        this.shortCode = shortCode;
        this.longUrl = longUrl;
        this.createdAt = createdAt;
    }

    /**
     * Gets the short code.
     * @return the short code
     */
    public String getShortCode() {
        return shortCode;
    }

    /**
     * Gets the original long URL.
     * @return the long URL
     */
    public String getLongUrl() {
        return longUrl;
    }

    /**
     * Gets the creation timestamp.
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

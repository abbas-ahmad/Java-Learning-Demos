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
     * The timestamp when the mapping expires (optional, may be null for no expiry).
     */
    private final LocalDateTime expiresAt;

    /**
     * Constructs a new URLMapping with expiry.
     * @param shortCode the unique short code
     * @param longUrl the original long URL
     * @param createdAt the creation timestamp
     * @param expiresAt the expiry timestamp (nullable)
     */
    public URLMapping(String shortCode, String longUrl, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.shortCode = shortCode;
        this.longUrl = longUrl;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    /**
     * Backward compatible constructor (no expiry).
     */
    public URLMapping(String shortCode, String longUrl, LocalDateTime createdAt) {
        this(shortCode, longUrl, createdAt, null);
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

    /**
     * Gets the expiry timestamp (nullable).
     */
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    /**
     * Returns true if this mapping is expired (now > expiresAt), false otherwise or if no expiry.
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}
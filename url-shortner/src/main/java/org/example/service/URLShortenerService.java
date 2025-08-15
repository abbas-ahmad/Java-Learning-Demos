package org.example.service;

/**
 * Service interface for shortening URLs and resolving short codes to original URLs.
 * Implementations provide the core business logic for the URL shortener.
 */
public interface URLShortenerService {
    /**
     * Shortens a given long URL and returns a unique short code.
     * @param longUrl the original long URL
     * @return the generated short code
     */
    String shortenUrl(String longUrl);

    /**
     * Shortens a given long URL with an expiry time and returns a unique short code.
     * If expiresAt is null, the mapping does not expire.
     * @param longUrl the original long URL
     * @param expiresAt the expiry timestamp (nullable)
     * @return the generated short code
     */
    String shortenUrl(String longUrl, java.time.LocalDateTime expiresAt);

    /**
     * Retrieves the original long URL for a given short code.
     * @param shortCode the short code
     * @return the original long URL, or null if not found
     */
    String getOriginalUrl(String shortCode);
}

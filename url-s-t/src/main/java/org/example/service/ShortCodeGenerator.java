package org.example.service;

/**
 * Interface for generating unique short codes for long URLs.
 * Implementations may use hashing, counters, or other algorithms.
 */
public interface ShortCodeGenerator {
    /**
     * Generates a unique short code for the given long URL.
     * @param longUrl the original long URL
     * @return a unique short code
     */
    String generateShortCode(String longUrl);
}

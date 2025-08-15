package org.example.service;

import org.example.model.URLMapping;
import org.example.repository.URLRepository;
import org.example.util.UrlValidator;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;

/**
 * Implementation of the URLShortenerService interface.
 * Handles the business logic for shortening URLs and resolving short codes.
 * Uses a repository for storage and a generator for code creation.
 */
public class URLShortenerServiceImpl implements URLShortenerService {
    /**
     * Repository for storing and retrieving URL mappings.
     */
    private final URLRepository repository;
    /**
     * Generator for creating unique short codes.
     */
    private final ShortCodeGenerator generator;

    /**
     * Constructs a new URLShortenerServiceImpl.
     * @param repository the URL mapping repository
     * @param generator the short code generator
     */
    public URLShortenerServiceImpl(URLRepository repository, ShortCodeGenerator generator) {
        this.repository = repository;
        this.generator = generator;
    }

    /**
     * {@inheritDoc}
     * If the URL was already shortened, returns the existing short code.
     */
    @Override
    public String shortenUrl(String longUrl) {
        UrlValidator.validate(longUrl);
        // Backward compatible: no expiry
        return shortenUrl(longUrl, null);
    }

    @Override
    public String shortenUrl(String longUrl, LocalDateTime expiresAt) {
        UrlValidator.validate(longUrl);
        URLMapping existing = repository.findByLongUrl(longUrl);
        if (existing != null) {
            // If the existing mapping is expired, allow re-shortening
            if (!existing.isExpired()) {
                return existing.getShortCode();
            }
        }
        // Generate a new short code
        String shortCode = generator.generateShortCode(longUrl);
        int maxAttempts = 10;
        int attempts = 1;
        while (repository.findByShortCode(shortCode) != null && attempts < maxAttempts) {
            shortCode = generator.generateShortCode(longUrl);
            attempts++;
        }
        if (repository.findByShortCode(shortCode) != null) {
            throw new IllegalStateException("Failed to generate a unique short code after " + maxAttempts + " attempts");
        }
        URLMapping mapping = new URLMapping(shortCode, longUrl, LocalDateTime.now(), expiresAt);
        repository.save(mapping);
        return shortCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOriginalUrl(String shortCode) {
        URLMapping mapping = repository.findByShortCode(shortCode);
        if (mapping == null || mapping.isExpired()) {
            return null;
        }
        return mapping.getLongUrl();
    }
}

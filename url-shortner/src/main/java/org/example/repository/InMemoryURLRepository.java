package org.example.repository;

import org.example.model.URLMapping;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the URLRepository interface.
 * Stores URL mappings in thread-safe maps for both short code and long URL lookups.
 * Suitable for development, testing, or single-instance deployments.
 * Not recommended for production-scale distributed systems.
 */
public class InMemoryURLRepository implements URLRepository {
    /**
     * Maps short codes to URLMapping objects for fast lookup by code.
     */
    private final Map<String, URLMapping> codeToMapping = new ConcurrentHashMap<>();
    /**
     * Maps long URLs to URLMapping objects for fast lookup by original URL.
     */
    private final Map<String, URLMapping> urlToMapping = new ConcurrentHashMap<>();

    /**
     * Lock object for synchronizing compound operations to ensure atomicity and consistency.
     */
    private final Object lock = new Object();

    /**
     * Saves a URL mapping in both lookup maps atomically.
     * @param mapping the URLMapping to save
     */
    @Override
    public void save(URLMapping mapping) {
        synchronized (lock) {
            codeToMapping.put(mapping.getShortCode(), mapping);
            urlToMapping.put(mapping.getLongUrl(), mapping);
        }
    }

    /**
     * Finds a URL mapping by its short code.
     * @param shortCode the short code
     * @return the URLMapping if found, otherwise null
     */
    @Override
    public URLMapping findByShortCode(String shortCode) {
        return codeToMapping.get(shortCode);
    }

    /**
     * Finds a URL mapping by its original long URL.
     * @param longUrl the original long URL
     * @return the URLMapping if found, otherwise null
     */
    @Override
    public URLMapping findByLongUrl(String longUrl) {
        return urlToMapping.get(longUrl);
    }
}

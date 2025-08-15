package org.example.repository;

import org.example.model.URLMapping;

/**
 * Repository interface for storing and retrieving URL mappings.
 * Implementations may use in-memory, database, or distributed storage.
 */
public interface URLRepository {
    /**
     * Saves a URL mapping.
     * @param mapping the URLMapping to save
     */
    void save(URLMapping mapping);

    /**
     * Finds a URL mapping by its short code.
     * @param shortCode the short code
     * @return the URLMapping if found, otherwise null
     */
    URLMapping findByShortCode(String shortCode);

    /**
     * Finds a URL mapping by its original long URL.
     * @param longUrl the original long URL
     * @return the URLMapping if found, otherwise null
     */
    URLMapping findByLongUrl(String longUrl);
}

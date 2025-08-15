package org.example.service;

import org.example.repository.URLRepository;
import org.example.service.ShortCodeGenerator;
import org.example.model.URLMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the URLShortenerService implementation.
 * Uses Mockito to mock dependencies and verify service behavior.
 */
class URLShortenerServiceTest {
    private URLRepository repository;
    private ShortCodeGenerator generator;
    private URLShortenerService service;

    /**
     * Sets up the test environment by mocking dependencies and initializing the service.
     */
    @BeforeEach
    void setUp() {
        repository = mock(URLRepository.class);
        generator = mock(ShortCodeGenerator.class);
        service = new URLShortenerServiceImpl(repository, generator);
    }

    /**
     * Tests that shortenUrl returns the expected short code and saves the mapping.
     */
    @Test
    void testShortenUrlReturnsShortCode() {
        String longUrl = "https://example.com/abc";
        String shortCode = "xyz123";
        when(generator.generateShortCode(longUrl)).thenReturn(shortCode);
        when(repository.findByLongUrl(longUrl)).thenReturn(null);

        String result = service.shortenUrl(longUrl);
        assertEquals(shortCode, result);
        verify(repository).save(any(URLMapping.class));
    }

    /**
     * Tests that getOriginalUrl returns the correct long URL for a given short code.
     */
    @Test
    void testGetOriginalUrlReturnsLongUrl() {
        String shortCode = "xyz123";
        String longUrl = "https://example.com/abc";
        URLMapping mapping = new URLMapping(shortCode, longUrl, LocalDateTime.now());
        when(repository.findByShortCode(shortCode)).thenReturn(mapping);

        String result = service.getOriginalUrl(shortCode);
        assertEquals(longUrl, result);
    }
}

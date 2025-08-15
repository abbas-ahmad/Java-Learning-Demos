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

    /**
     * Tests that shortenUrl retries if a generated short code collides, ensuring uniqueness.
     */
    @Test
    void testShortenUrlRetriesOnCollision() {
        String longUrl = "https://example.com/collision";
        String duplicateCode = "dup123";
        String uniqueCode = "uniq456";
        // First generated code collides, second is unique
        when(generator.generateShortCode(longUrl)).thenReturn(duplicateCode, uniqueCode);
        when(repository.findByLongUrl(longUrl)).thenReturn(null);
        when(repository.findByShortCode(duplicateCode)).thenReturn(new URLMapping(duplicateCode, "other", LocalDateTime.now()));
        when(repository.findByShortCode(uniqueCode)).thenReturn(null);

        String result = service.shortenUrl(longUrl);
        assertEquals(uniqueCode, result);
        verify(repository).save(any(URLMapping.class));
        verify(generator, times(2)).generateShortCode(longUrl);
    }

    @Test
    void shouldGiveExistingCodeIfAlreadyShortened(){
        String existingUrl = "https://example.com/existing";
        String existingCode = "existing123";

        when(repository.findByLongUrl(existingUrl)).thenReturn(
                new URLMapping(existingCode, existingUrl, LocalDateTime.now()));

        String shortened = service.shortenUrl(existingUrl);

        assertNotNull(shortened);
        assertEquals(existingCode, shortened);
    }

    @Test
    void shouldNotGiveExistingCodeIfNotShortened(){
        String existingUrl = "https://example.com/existing";
        String existingCode = "existing123";

        when(repository.findByLongUrl(existingUrl)).thenReturn(
                new URLMapping("newCode", existingUrl, LocalDateTime.now()));

        String shortened = service.shortenUrl(existingUrl);

        assertNotNull(shortened);
        assertNotEquals(existingCode, shortened);
        assertEquals("newCode", shortened);
    }

    @Test
    void shouldThrowExceptionWhenMaxAttemptsReached(){
        String longUrl = "https://example.com/maxattempts";

        when(repository.findByLongUrl(longUrl)).thenReturn(null);

        String generatedShortCode = generator.generateShortCode(longUrl);

        when(repository.findByShortCode(generatedShortCode)).thenReturn(
                new URLMapping("existingCode", longUrl, LocalDateTime.now()));

        assertThrows(IllegalStateException.class, () ->{
            service.shortenUrl(longUrl);
        });
    }

    @Test
    void shouldReturnNullWhenCodeNotFound(){
        String shortUrl = "notFound";

        when(repository.findByShortCode(shortUrl)).thenReturn(null);

        String originalUrl = service.getOriginalUrl(shortUrl);

        assertNull(originalUrl);
    }

    @Test
    void shouldReturnNewCodeWhenExistingCodeExpired(){
        String longUrl = "https://example.com/expired";
        String expiredCode = "expired";
        String newCode = "newCode";
        LocalDateTime expiredAt = LocalDateTime.now().minusMinutes(10);
        when(repository.findByLongUrl(longUrl)).thenReturn(
                new URLMapping(
                            expiredCode,
                            longUrl,
                            LocalDateTime.now(),
                            expiredAt)); // Set expiresAt in the past
        when(generator.generateShortCode(longUrl)).thenReturn(newCode);
        when(repository.findByShortCode(newCode)).thenReturn(null);

        String shortedUrl = service.shortenUrl(longUrl);
        assertNotNull(shortedUrl);
        assertNotEquals(expiredCode, shortedUrl);
        assertEquals(newCode, shortedUrl);
    }
}

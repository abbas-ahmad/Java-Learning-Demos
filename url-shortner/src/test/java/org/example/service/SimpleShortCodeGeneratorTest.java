package org.example.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the generateShortCode method of SimpleShortCodeGenerator.
 * This test checks if the method generates a short code that is not null or empty.
 */
@ExtendWith(MockitoExtension.class)
class SimpleShortCodeGeneratorTest {

    @InjectMocks
    private SimpleShortCodeGenerator generator;

    @Test
    void shouldGenerateShortCode(){
        String longUrl = "https://example.com/long-url";

        String shortCode = generator.generateShortCode(longUrl);

        assertNotNull(shortCode, "Short code should not be null");
        assertFalse(shortCode.isEmpty(), "Short code should not be empty");
    }
}
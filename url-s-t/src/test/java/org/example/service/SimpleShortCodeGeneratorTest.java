package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleShortCodeGeneratorTest {

    /**
     * Tests the generateShortCode method of SimpleShortCodeGenerator.
     * This test checks if the method generates a short code that is not null or empty.
     */
    private ShortCodeGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new SimpleShortCodeGenerator();
    }

    @Test
    void shouldGenerateShortCode(){
        String longUrl = "https://example.com/long-url";

        String shortCode = generator.generateShortCode(longUrl);

        assertNotNull(shortCode, "Short code should not be null");
        assertFalse(shortCode.isEmpty(), "Short code should not be empty");
    }
}
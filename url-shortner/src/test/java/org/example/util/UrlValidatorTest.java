package org.example.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;


import static org.junit.jupiter.api.Assertions.*;
class UrlValidatorTest {

    @ParameterizedTest
    @CsvSource({
            "http://example.com",
            "https://example.com",
            "http://www.example.com",
            "https://www.example.com",
            "http://example.com/path/to/resource",
            "https://example.com/path/to/resource?query=param#fragment"
    })
    void shouldReturnTrueForValidHttpUrls(String url) {
        assertDoesNotThrow(() -> UrlValidator.validate(url));
    }

    @ParameterizedTest
    @CsvSource({
            "ftp://example.com/file",
            "file:///C:/test.txt",
            "mailto:user@example.com",
            "htp://example.com",
            "http://",
            "https://",
            "http://?",
            "https://?",
            "http://#fragment",
            "https://#fragment"
    })
    void shouldThrowForInvalidUrls(String url) {
        assertThrows(IllegalArgumentException.class, () -> UrlValidator.validate(url));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    void shouldThrowForNullOrEmptyUrls(String url) {
        assertThrows(IllegalArgumentException.class, () -> UrlValidator.validate(url));
    }
}
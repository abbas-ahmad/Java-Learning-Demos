package org.example.service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Simple implementation of ShortCodeGenerator using a thread-safe counter and base62 encoding.
 * Each call generates a new unique short code.
 */
public class SimpleShortCodeGenerator implements ShortCodeGenerator {
    /**
     * Atomic counter to ensure unique IDs across threads.
     */
    private final AtomicLong counter = new AtomicLong(100000);
    /**
     * The alphabet used for base62 encoding.
     */
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    /**
     * The base for encoding (62).
     */
    private static final int BASE = ALPHABET.length();

    /**
     * Generates a unique short code for the given long URL using a counter and base62 encoding.
     * @param longUrl the original long URL (not used in this implementation)
     * @return a unique short code
     */
    @Override
    public String generateShortCode(String longUrl) {
        long id = counter.getAndIncrement();
        return encodeBase62(id);
    }

    /**
     * Encodes a number into a base62 string.
     * @param num the number to encode
     * @return the base62-encoded string
     */
    private String encodeBase62(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int lastDigit = (int)(num % BASE);
            char ch = ALPHABET.charAt(lastDigit);
            sb.append(ch);
            num /= BASE;
        }
        return sb.reverse().toString();
    }

}

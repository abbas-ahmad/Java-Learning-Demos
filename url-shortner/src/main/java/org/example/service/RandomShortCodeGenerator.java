package org.example.service;

import java.security.SecureRandom;

/**
 * Generates random short codes of configurable length using Base62 characters.
 * Suitable for distributed environments with collision checking.
 */
public class RandomShortCodeGenerator implements ShortCodeGenerator {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();
    private final int codeLength;

    /**
     * Creates a generator for random short codes of the given length.
     * @param codeLength the length of the short code (e.g., 6-8)
     */
    public RandomShortCodeGenerator(int codeLength) {
        if (codeLength < 4 || codeLength > 12) throw new IllegalArgumentException("Code length should be 4-12");
        this.codeLength = codeLength;
    }

    @Override
    public String generateShortCode(String longUrl) {
        StringBuilder sb = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++) {
            int idx = RANDOM.nextInt(BASE62.length());
            sb.append(BASE62.charAt(idx));
        }
        return sb.toString();
    }
}


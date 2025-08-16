package org.example.service;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RandomShortCodeGeneratorTest {

    @Test
    void generatesCodesOfCorrectLengthAndUniqueness() {
        int codeLength = 7;
        ShortCodeGenerator generator = new RandomShortCodeGenerator(codeLength);
        Set<String> codes = new HashSet<>();
        int count = 10000;
        for (int i = 0; i < count; i++) {
            String code = generator.generateShortCode("https://example.com/" + i);
            assertNotNull(code);
            assertEquals(codeLength, code.length(), "Code should be of length " + codeLength);
            assertTrue(codes.add(code), "Duplicate code generated: " + code);
        }
    }

    @Test
    void shouldThrowExceptionWhenCodeLengthIsInvalid(){
        int invalidLength = 3; // Below minimum length

        assertThrows(IllegalArgumentException.class,
                () -> new RandomShortCodeGenerator(invalidLength));
    }

    @Test
    void shouldThrowExceptionWhenCodeLengthIsInvalid2(){
        int invalidLength = 13; // Below minimum length

        assertThrows(IllegalArgumentException.class,
                () -> new RandomShortCodeGenerator(invalidLength));
    }

}


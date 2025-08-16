package org.example.repository;

import org.example.model.URLMapping;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryURLRepositoryConcurrencyTest {

    @Test
    void shouldHandleConcurrentSavesAndLookups() throws InterruptedException {
        InMemoryURLRepository repo = new InMemoryURLRepository();
        int threadCount = 20;
        int urlsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Set<String> allShortCodes = new HashSet<>();
        Set<String> allLongUrls = new HashSet<>();

        for (int t = 0; t < threadCount; t++) {
            int threadNum = t;
            executor.submit(() -> {
                for (int i = 0; i < urlsPerThread; i++) {
                    String code = "code-" + threadNum + "-" + i;
                    String url = "https://example.com/" + threadNum + "/" + i;
                    URLMapping mapping = new URLMapping(code, url, LocalDateTime.now());
                    repo.save(mapping);
                    synchronized (allShortCodes) { allShortCodes.add(code); }
                    synchronized (allLongUrls) { allLongUrls.add(url); }
                }
                latch.countDown();
            });
        }
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        // Validate all mappings are present and correct
        for (String code : allShortCodes) {
            URLMapping mapping = repo.findByShortCode(code);
            assertNotNull(mapping, "Mapping missing for code: " + code);
            assertEquals(code, mapping.getShortCode());
        }
        for (String url : allLongUrls) {
            URLMapping mapping = repo.findByLongUrl(url);
            assertNotNull(mapping, "Mapping missing for url: " + url);
            assertEquals(url, mapping.getLongUrl());
        }
    }
}


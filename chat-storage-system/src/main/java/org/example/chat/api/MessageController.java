package org.example.chat.api;

import org.example.chat.api.dto.MessageDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/v1")
public class MessageController {

    // In-memory store for Phase-0 MVP
    private final Map<UUID, List<MessageDto>> store = new ConcurrentHashMap<>();

    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<MessageDto> postMessage(@PathVariable UUID conversationId,
                                                  @RequestBody MessageDto req) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        MessageDto msg = new MessageDto(id, conversationId, req.getSenderId(), req.getContent(),
                req.getContentType() == null ? "text" : req.getContentType(), now);
        store.computeIfAbsent(conversationId, k -> new CopyOnWriteArrayList<>()).add(0, msg);
        return ResponseEntity.status(HttpStatus.CREATED).body(msg);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageDto>> listMessages(@PathVariable UUID conversationId,
                                                         @RequestParam(defaultValue = "50") int limit) {
        List<MessageDto> list = store.getOrDefault(conversationId, Collections.emptyList());
        int to = Math.min(limit, list.size());
        return ResponseEntity.ok(list.subList(0, to));
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<MessageDto> getMessage(@PathVariable UUID id) {
        // naive linear scan for MVP
        for (List<MessageDto> msgs : store.values()) {
            for (MessageDto m : msgs) {
                if (m.getId().equals(id)) {
                    return ResponseEntity.ok(m);
                }
            }
        }
        return ResponseEntity.notFound().build();
    }
}


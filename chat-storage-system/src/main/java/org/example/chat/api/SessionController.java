package org.example.chat.api;

import jakarta.validation.Valid;
import org.example.chat.api.dto.CreateSessionRequest;
import org.example.chat.api.dto.SessionResponse;
import org.example.chat.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/sessions")
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody CreateSessionRequest req) {
        SessionResponse s = sessionService.createSession(req);
        return ResponseEntity.status(201).body(s);
    }

    @GetMapping("/users/{userId}/sessions")
    public ResponseEntity<List<SessionResponse>> listSessions(@PathVariable UUID userId) {
        List<SessionResponse> list = sessionService.listSessions(userId);
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/sessions/{sessionId}/rename")
    public ResponseEntity<SessionResponse> rename(@PathVariable UUID sessionId, @RequestParam String title) {
        return ResponseEntity.ok(sessionService.renameSession(sessionId, title));
    }

    @PatchMapping("/sessions/{sessionId}/favorite")
    public ResponseEntity<Void> favorite(@PathVariable UUID sessionId, @RequestParam boolean fav) {
        sessionService.setFavorite(sessionId, fav);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> delete(@PathVariable UUID sessionId) {
        sessionService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}


package org.example.chat.service;

import org.example.chat.api.dto.CreateSessionRequest;
import org.example.chat.api.dto.SessionResponse;
import org.example.chat.domain.ChatSession;
import org.example.chat.exception.ResourceNotFoundException;
import org.example.chat.repository.ChatSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SessionService {

    private final ChatSessionRepository sessionRepository;

    public SessionService(ChatSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public SessionResponse createSession(CreateSessionRequest req) {
        ChatSession s = new ChatSession();
        s.setUserId(req.getUserId());
        s.setTitle(req.getTitle());
        ChatSession saved = sessionRepository.save(s);
        return toDto(saved);
    }

    public List<SessionResponse> listSessions(UUID userId) {
        return sessionRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SessionResponse renameSession(UUID sessionId, String title) {
        ChatSession s = sessionRepository.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("ChatSession", sessionId.toString()));
        s.setTitle(title);
        return toDto(sessionRepository.save(s));
    }

    @Transactional
    public void setFavorite(UUID sessionId, boolean favorite) {
        ChatSession s = sessionRepository.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("ChatSession", sessionId.toString()));
        s.setFavorite(favorite);
        sessionRepository.save(s);
    }

    @Transactional
    public void deleteSession(UUID sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("ChatSession", sessionId.toString());
        }
        sessionRepository.deleteById(sessionId);
    }

    private SessionResponse toDto(ChatSession s) {
        return new SessionResponse(s.getId(), s.getTitle(), s.getUserId(), s.isFavorite(), s.getCreatedAt(), s.getUpdatedAt());
    }
}

package org.example.chat.service;

import org.example.chat.api.dto.CreateMessageRequest;
import org.example.chat.api.dto.MessageResponse;
import org.example.chat.domain.ChatSession;
import org.example.chat.domain.Message;
import org.example.chat.exception.ResourceNotFoundException;
import org.example.chat.repository.ChatSessionRepository;
import org.example.chat.repository.MessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatSessionRepository sessionRepository;

    public MessageService(MessageRepository messageRepository, ChatSessionRepository sessionRepository) {
        this.messageRepository = messageRepository;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public MessageResponse addMessage(UUID sessionId, CreateMessageRequest req) {
        ChatSession session = sessionRepository.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("ChatSession", sessionId.toString()));
        Message m = new Message();
        m.setSession(session);
        m.setSenderId(req.getSenderId());
        m.setRole(req.getRole());
        m.setContent(req.getContent());
        m.setContext(req.getContext());
        Message saved = messageRepository.save(m);
        return toDto(saved);
    }

    public List<MessageResponse> listMessages(UUID sessionId, int limit) {
        ChatSession session = sessionRepository.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("ChatSession", sessionId.toString()));
        return messageRepository.findBySessionOrderByCreatedAtDesc(session, PageRequest.of(0, limit)).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public MessageResponse getMessage(UUID messageId) {
        Message m = messageRepository.findById(messageId).orElseThrow(() -> new ResourceNotFoundException("Message", messageId.toString()));
        return toDto(m);
    }

    private MessageResponse toDto(Message m) {
        List<String> attachments = m.getAttachments().stream().map(a -> a.getPath()).collect(Collectors.toList());
        return new MessageResponse(m.getId(), m.getSession().getId(), m.getSenderId(), m.getRole(), m.getContent(), m.getContext(), m.getCreatedAt(), attachments);
    }
}

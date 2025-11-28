package org.example.chat.api;

import jakarta.validation.Valid;
import org.example.chat.api.dto.CreateMessageRequest;
import org.example.chat.api.dto.MessageResponse;
import org.example.chat.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Validated
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<MessageResponse> postMessage(@PathVariable UUID sessionId,
                                                       @Valid @RequestBody CreateMessageRequest req) {
        MessageResponse msg = messageService.addMessage(sessionId, req);
        return ResponseEntity.status(201).body(msg);
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<List<MessageResponse>> listMessages(@PathVariable UUID sessionId,
                                                               @RequestParam(defaultValue = "50") int limit) {
        List<MessageResponse> list = messageService.listMessages(sessionId, limit);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<MessageResponse> getMessage(@PathVariable UUID id) {
        MessageResponse m = messageService.getMessage(id);
        return ResponseEntity.ok(m);
    }
}

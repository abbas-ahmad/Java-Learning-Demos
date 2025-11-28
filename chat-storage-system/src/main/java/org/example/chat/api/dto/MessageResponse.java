package org.example.chat.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
    private UUID id;
    private UUID sessionId;
    private UUID senderId;
    private String role;
    private String content;
    private String context;
    private Instant createdAt;
    private List<String> attachments;
}

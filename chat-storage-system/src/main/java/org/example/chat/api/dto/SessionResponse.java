package org.example.chat.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionResponse {
    private UUID id;
    private String title;
    private UUID userId;
    private boolean favorite;
    private Instant createdAt;
    private Instant updatedAt;
}

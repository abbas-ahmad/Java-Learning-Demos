package org.example.chat.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMessageRequest {
    @NotNull
    private UUID senderId;

    @NotBlank
    @Size(max = 50)
    private String role; // user|assistant|system

    @NotBlank
    @Size(max = 20000)
    private String content;

    private String context;
}

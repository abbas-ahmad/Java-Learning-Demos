package org.example.chat.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "message")
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Message {

    @Id
    @Column(name = "id", nullable = false)
    @ToString.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @ToString.Exclude
    private ChatSession session;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "role", nullable = false)
    private String role; // user | assistant | system

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "context", columnDefinition = "TEXT")
    private String context;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        createdAt = Instant.now();
    }
}

package org.example.chat.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "attachment")
@Getter
@Setter
@NoArgsConstructor
public class Attachment {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Column(name = "path")
    private String path; // S3 URL or storage path

    @Column(name = "name")
    private String name;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "size")
    private Long size;

    @Column(name = "checksum")
    private String checksum;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }
}

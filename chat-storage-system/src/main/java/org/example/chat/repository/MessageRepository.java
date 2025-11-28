package org.example.chat.repository;

import org.example.chat.domain.Message;
import org.example.chat.domain.ChatSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findBySessionOrderByCreatedAtDesc(ChatSession session, Pageable pageable);
}


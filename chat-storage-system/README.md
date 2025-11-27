# Chat Storage Service (Phase 0)

Minimal Phase-0 skeleton for the Chat Storage Service.

Run locally (requires JDK 21 & Gradle wrapper):

```bash
cd chat-storage-system
./gradlew bootRun
```

API endpoints (local):
- POST /api/v1/conversations/{conversationId}/messages
- GET /api/v1/conversations/{conversationId}/messages
- GET /api/v1/messages/{id}

This MVP uses an in-memory store for messages; Phase 1 will add Postgres, Flyway, and Kafka-based ingestion.


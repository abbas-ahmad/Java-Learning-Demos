# Chat Storage Service — Specifications

Status: Draft
Author: Automated plan (will iterate)
Date: 2025-11-27

## 1. Purpose and Goal
This document defines the architecture and implementation plan for the Chat Storage Service required by the Backend Developer Case Study in `chat-storage-system/Backend Developer Case Study.docx`.

Primary goals
- Provide a highly available, durable, and auditable service to store and retrieve chat messages and metadata.
- Support production-grade quality: Java 21 + Spring Boot, observability, CI/CD, security, and infra-as-code deployment.
- Design for scale (configurable): from low QPS (<100) to high throughput (10k+ messages/sec) with options.

Non-functional requirements (NFR)
- Durability: persisted messages should not be lost (replication, WAL, durable queues).
- Availability: 99.9% SLA target for reads, eventual for writes if async.
- Latency: Reads in <50ms median for common queries; write latency depends on sync/async mode.
- Retention: configurable (default 90 days) with compaction/archive policies.
- Compliance: support encryption at rest, access controls, and audit logging.

Assumptions (if not provided in docx)
- Messages are immutable once written (edits create new message versions or separate edit records).
- Attachments may be present and are stored outside primary DB (object store).
- Client authentication provided via API gateway (JWT/OAuth2); service enforces scopes.
- Throughput expectations: default conservative target 1k msg/s. If >5k msg/s, use Kafka + partitioning.

Decisions (recommended)
- Use Postgres (managed, e.g., AWS RDS / Cloud SQL) as primary store for metadata and queries.
- Use Kafka as an ingestion buffer for high-throughput write paths and to decouple producer latency.
- Use S3-compatible object storage for attachments.
- Use Spring Boot (Java 21) with layered modules: api, service, persistence, ingest, jobs.

## 2. High-level Architecture

Components
- API Gateway (outscope): TLS, auth, rate-limiting, routing.
- Chat Storage Service (this project): REST + optional gRPC API, business logic, persistence layer.
- Message Ingest (Kafka topic): persisted messages for async processing.
- Postgres primary DB: conversation, message, participant, indexes.
- Object Storage (S3): attachments.
- Background Jobs: retention, archive, compaction, metrics export.
- Observability: Prometheus, Grafana, distributed traces (OpenTelemetry), structured logs.
- CI/CD: GitHub Actions or GitLab CI -> build/test -> container image -> registry -> Kubernetes deploy (Helm).

Flow patterns
- Synchronous write: API -> validate -> DB transaction insert -> response (stronger consistency, higher latency)
- Asynchronous write (recommended for high throughput): API -> validate -> produce to Kafka -> respond 202 -> consumer -> persist to DB + produce events
- Read: API -> query DB with pagination & indexes

## 3. Domain Model & Schema

Core entities (conceptual)
- Conversation
  - id (UUID)
  - type (one-to-one, group)
  - created_at
  - updated_at
  - metadata (jsonb)
- Participant
  - id (UUID)
  - conversation_id (UUID)
  - user_id (UUID)
  - joined_at
  - role (enum)
- Message
  - id (UUID)
  - conversation_id (UUID)
  - sender_id (UUID)
  - content (text)
  - content_type (enum: text, markdown, html)
  - attachments (jsonb array of attachment metas)
  - created_at (timestamp with timezone)
  - delivered_at (nullable)
  - metadata (jsonb)
- Attachment (metas only in DB)
  - id (UUID)
  - message_id (UUID)
  - storage_path (S3 URI)
  - mime_type
  - size
  - checksum

Suggested Postgres schema (DDL snippet)

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE conversation (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  type varchar(32) NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  metadata jsonb
);

CREATE TABLE participant (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  conversation_id uuid REFERENCES conversation(id) ON DELETE CASCADE,
  user_id uuid NOT NULL,
  joined_at timestamptz NOT NULL DEFAULT now(),
  role varchar(32),
  UNIQUE(conversation_id, user_id)
);

CREATE INDEX idx_participant_conversation ON participant(conversation_id);

CREATE TABLE message (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  conversation_id uuid REFERENCES conversation(id) ON DELETE CASCADE,
  sender_id uuid NOT NULL,
  content text,
  content_type varchar(32) DEFAULT 'text',
  attachments jsonb,
  created_at timestamptz NOT NULL DEFAULT now(),
  metadata jsonb
);

CREATE INDEX idx_message_conversation_created_at ON message(conversation_id, created_at DESC);

Note: attachments are stored in object storage; DB stores metadata and S3 path.

## 4. API Design
Design principles
- RESTful HTTP JSON API (HTTPs only). Keep endpoints minimal and versioned: /api/v1
- Provide pagination for list endpoints (cursor-based or offset; prefer cursor for scale)
- Provide idempotency for write endpoints (Idempotency-Key header) when using sync writes.
- Support both sync and async ingestion via query param or header (X-Write-Mode: sync|async).

Endpoints overview
- POST /api/v1/conversations
  - Create conversation
  - Request: { type, participants: [userId], metadata }
  - Response: 201 { conversation }
- GET /api/v1/conversations/{id}
  - Get conversation metadata
- POST /api/v1/conversations/{id}/messages
  - Add message
  - Request: { senderId, content, contentType?, attachments?: [{filename, size, mime, tempUploadId}] }
  - Query param/header: ?mode=sync|async or X-Write-Mode
  - Response: 201 (sync) { message } or 202 (async) { messageId, status: "queued" }
- GET /api/v1/conversations/{id}/messages?limit=50&cursor=...
  - List messages, reverse chronological by default, cursor-based pagination
- GET /api/v1/messages/{id}
  - Retrieve single message
- PUT /api/v1/messages/{id}/ack
  - Mark delivered/read metadata (small patch)
- POST /api/v1/uploads
  - Presigned upload for attachments: request { filename, mime, size, conversationId } -> returns presigned URL + uploadId

Events (internal/public)
- message-stored topic (Kafka) for downstream consumers (notifications, search index)
- message-deleted/archive events

API Contracts: Provide example JSON schemas and validation rules (in codebase via DTO + validation annotations)

## 5. Service Design & Components
Project modular layout (maven/gradle modules)
- chat-api (controllers, DTOs, REST config)
- chat-service (business logic, domain services)
- chat-persistence (repositories, JPA / JDBC templates)
- chat-ingest (Kafka producers/consumers)
- chat-jobs (scheduled tasks)
- chat-app (Spring Boot main starter)

Key libraries and frameworks
- Spring Boot 3.x (compatible with Java 21)
- Spring Data JDBC or Spring Data JPA (Postgres)
- Flyway or Liquibase for schema migrations
- Kafka client / Spring Kafka (if using async ingestion)
- AWS S3 SDK or S3-compatible client (MinIO support)
- OpenTelemetry + Micrometer + Prometheus
- Testing: JUnit 5, Mockito, Testcontainers for integration tests

Transactional boundaries
- Writes that update multiple tables should be transactional within DB consumer processing.
- Async path: produce to Kafka (non-transactional or transactional producer depending on guarantee). Consumer will persist within DB transaction.

Idempotency
- Writes include client-provided idempotency key or dedupe via message-id in payload to avoid duplicates from retries.

## 6. Scalability & Performance
- Read scaling: scale replicas (read-only) or use read-optimized replicas; use caches (Redis) for hot conversation state.
- Write scaling: partition conversations/messages logically and use Kafka partition key = conversationId for message order per conversation.
- Sharding: If single Postgres can't handle throughput, move messages to partitioned tables or use Cassandra for wide-column scalable writes.
- Indexing: keep indexes narrow and monitor bloat; use partial indexes for retention windows.

## 7. Retention, Archival & Deletion
- Retention policy configurable per-tenant or global.
- Background job runs daily to move expired messages to archive storage (S3) or to delete them.
- Soft delete flag (deleted_at) kept for short grace window before permanent purge.

## 8. Security
- TLS everywhere.
- AuthN/AuthZ: JWT bearer tokens validated by API gateway; enforce scopes in service.
- Input validation and size limits for messages/attachments.
- Encryption at rest: enable at DB and S3 layer; application-level encryption for sensitive fields as needed.
- Secrets management: use cloud KMS/Hashicorp Vault for DB passwords and KMS keys.
- Audit logging: write security-relevant events (create/delete) to append-only audit log (or external SIEM).

## 9. Observability & SLOs
- Metrics: request latency, request rate, error rate, DB latency, Kafka lag, retention job durations.
- Tracing: instrument key flows with OpenTelemetry (incoming requests, producer/consumer operations, DB calls).
- Logs: structured JSON logs, correlate requestId across services.
- Alerts: error rate > threshold, consumer lag > threshold, job failures, disk usage.

## 10. Testing Strategy
- Unit tests for domain and service layers.
- Integration tests with Testcontainers (Postgres, Kafka, MinIO) for persistence and ingest flow.
- Contract tests for API (OpenAPI + consumer-driven contract tests if other teams exist).
- Load tests: k6 or Gatling with representative traffic patterns.

## 11. CI/CD
- Build: Gradle build (Java 21 toolchain), run lint, tests, and build Docker image.
- Image scan: Snyk/Trivy for CVEs.
- Delivery: push to container registry, deploy to staging via Helm, run smoke tests, then promote to production.
- Rollout: Canary or Blue/Green deployments (Kubernetes + Istio/Ingress controller).

## 12. Deployment & Infra
- Containerize the app: small distroless or openjdk:21-jdk-slim base image.
- Helm chart + Kubernetes manifests (Deployment, Service, HPA, ConfigMap, Secret, PodDisruptionBudget).
- Managed Postgres (RDS/CloudSQL) with read-replicas and backups.
- Kafka cluster (managed MSK/Confluent or self-hosted) with adequate partitions.
- Object storage (S3/MinIO) with lifecycle rules for archive.
- Use Terraform for infra provisioning (network, DB, Kafka, object storage).

## 13. Runbook & Operational Playbook
- On-call runbook for major incidents: steps to check logs, consumer lag, DB health, queue, and revert deployment.
- Backups & restore: periodic snapshots and a tested restore plan.
- Capacity planning checklist and scaling steps.

## 14. Roadmap & Milestones
Phase 0 — MVP (2-4 weeks)
- Project skeleton (Spring Boot), REST API, Postgres persistence, Flyway migrations.
- Add message create (sync) and list endpoints.
- Unit tests and basic integration test with in-memory DB.
- Dockerfile and simple Kubernetes manifests.

Phase 1 — Production Hardening (4-8 weeks)
- Add Kafka async ingestion, consumer to persist messages.
- Attachments via presigned S3 URLs + storage metadata.
- Observability (Prometheus + tracing) and metrics dashboards.
- Security hardening (JWT validation, secrets management).

Phase 2 — Scalability & Compliance (8+ weeks)
- Load testing & tuning, partitioning and sharding options.
- Archive & retention jobs.
- Compliance features: encryption at rest, audit logs, data deletion workflows.

## 15. Deliverables & Repo Changes
Files to add in `chat-storage-system/`
- specifications.md (this file)
- chat-api module (controller + DTOs)
- chat-service module
- chat-persistence module with Flyway migrations (sql)
- chat-ingest module (Kafka producers/consumers)
- Dockerfile, Helm chart under `charts/chat-storage`
- CI workflow `.github/workflows/ci.yml`
- README.md with quickstart and debugging steps

## 16. Next Steps (Immediate)
- Confirm assumptions: target throughput, retention policy, sync vs async default, attachment support.
- Create a minimal Sprint-0 branch implementing Phase 0 skeleton (I can scaffold modules, build file, and a simple REST endpoint next).

---

Appendix: Example message JSON
{
  "id": "uuid-v4",
  "conversationId": "uuid-v4",
  "senderId": "uuid-v4",
  "content": "Hello world",
  "contentType": "text",
  "attachments": [],
  "createdAt": "2025-11-27T12:00:00Z",
  "metadata": {}
}





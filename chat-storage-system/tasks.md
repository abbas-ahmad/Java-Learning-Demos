# Tasks for RAG Chat Storage Microservice (production-ready)

Purpose: This task list captures everything needed to transform the Phase-0 skeleton into a production-grade RAG Chat Storage microservice that meets the interview case study requirements.

High-level plan (what I'll deliver)
- Phase 0: MVP (local-run) — core APIs, data model, in-memory or H2 persistence, OpenAPI, Dockerfile, env config, basic tests.
- Phase 1: Production Hardening — Postgres + Flyway, API key auth, rate limiting, centralized logging, global error handling, Docker Compose for local stack, integration tests with Testcontainers, CI pipeline.
- Phase 2: Scalability & Reliability — Kafka ingestion or synchronous writes depending on throughput, S3 attachments, retention jobs, observability (metrics, tracing), backups, secrets management.
- Phase 3: Infra-as-Code & Deployment — Helm chart, Kubernetes manifests, Canary rollout, monitoring & alerting runbooks, disaster recovery.

Checklist (short)
- [ ] Domain model (Session, Message, Attachment, Participant)
- [ ] REST API controllers + OpenAPI
- [ ] DTOs + validation
- [ ] Persistence (JPA + Flyway to Postgres)
- [ ] API Key auth middleware
- [ ] Rate limiting
- [ ] Logging, tracing, metrics
- [ ] Global error handling
- [ ] Dockerfile + docker-compose (Postgres, MinIO, Kafka optional)
- [ ] CI pipeline (build, test, image, scan)
- [ ] Integration & load tests
- [ ] Helm chart + Kubernetes manifests

---

PHASE 0 — MVP (fast, local, ~1-2 days)
Goal: Implement the minimal feature set with a runnable app locally using H2 or in-memory store.

Tasks
1. Project setup and dependencies
   - Add Spring Boot, Spring Web, Spring Data JPA, H2, Flyway, springdoc-openapi (already added), validation (jakarta.validation).
   - Files: `build.gradle` (done), `application.yml` (done)
   - Acceptance: `./gradlew bootRun` starts and Swagger UI available.

2. Domain model (JPA entities)
   - Create entities: `ChatSession`, `Message`, `Attachment` (meta), `SessionFavorite` or boolean on session.
   - Fields: sessionId (UUID), title, userId (owner), favorite (boolean), createdAt, updatedAt; Message: id, sessionId FK, role (user/assistant/system), content, context (jsonb/text), createdAt, metadata; Attachment: id, messageId, path, name, mime, size, checksum.
   - Files: `src/main/java/org/example/chat/domain/*` (entities)
   - Acceptance: Entities compile; JPA auto-creates schema in H2.

3. DTOs and Validation
   - Create request/response DTOs for sessions and messages with validation annotations (@NotNull, @Size).
   - Files: `api/dto/*`
   - Acceptance: Invalid input returns 400 with validation details.

4. Controllers & Services (sync writes)
   - Implement controllers for: create session, rename session, mark favorite/unfavorite, delete session, add message to session, get messages (paged), get session list.
   - Implement service layer (business logic) and repository layer (Spring Data JPA interfaces for now).
   - Files: `api/*`, `service/*`, `persistence/*`
   - Acceptance: Basic end-to-end flows work via curl or Swagger.

5. OpenAPI docs (swagger) and examples
   - Ensure endpoints are annotated for clear OpenAPI generation (springdoc handles it). Provide example request/response models and security scheme placeholder for API key.
   - Files: OpenAPI config (done), ensure DTOs have @Schema annotations as needed.
   - Acceptance: Swagger UI shows endpoints and example shapes.

6. Global error handling & structured logging
   - Add `@ControllerAdvice` global exception handler with meaningful error response model `{timestamp, path, status, error, message, errors[]}`.
   - Use structured JSON logging (Logback encoder optional but basic logs OK for Phase 0).
   - Acceptance: Exceptions return structured JSON and 5xx are logged.

7. Dockerfile + `.env.example`
   - Add a Dockerfile to build the app image; add `.env.example` with API_KEY, DB URL placeholders.
   - Files: `Dockerfile`, `.env.example`
   - Acceptance: `docker build -t chat-storage:0.1 .` succeeds.

8. README and quickstart
   - Add run instructions and example curl commands.

Testing
- Unit tests for service layer (mock repos). Integration smoke test using H2 for core flows.

---

PHASE 1 — Production Hardening (~1-2 weeks)
Goal: Make the app production-capable with Postgres, auth, rate limiting, centralized logging, CI.

Tasks
1. Postgres + Flyway migrations
   - Add Flyway SQL scripts to create the tables described in specifications.md.
   - Configure Spring profile `application-prod.yml` for Postgres connection.
   - Files: `db/migration/V1__init.sql`
   - Acceptance: App starts with Postgres and applies migrations.

2. Externalized configuration & env management
   - Use Spring Boot config via environment variables and `application-*.yml` profiles. Provide `.env.example` with all required env vars (API_KEY, DB_URL, S3 creds, KAFKA, etc.).
   - Acceptance: All secrets read from env; no hardcoded secrets.

3. API Key authentication middleware
   - Implement filter/interceptor that reads `X-Api-Key` header and compares with env var `CHAT_API_KEY` (support multiple keys via comma-separated list or lookup store).
   - Add an OpenAPI security scheme (apiKey type) so Swagger allows setting the header.
   - Files: `security/ApiKeyAuthFilter.java`, update OpenApiConfig.
   - Acceptance: Requests without key return 401; requests with invalid key return 403.

4. Rate limiting
   - Implement rate limiting using token-bucket solution (Bucket4j) or Spring filter with Redis backend for distributed rate limit.
   - Local fallback in-memory for single-instance development; production config uses Redis.
   - Files: `rate/RateLimitFilter.java` and `application.yml` settings.
   - Acceptance: Exceeding allowed QPS returns 429 with Retry-After header.

5. Centralized structured logging
   - Configure Logback to produce JSON logs (Logstash encoder) and include requestId, traceId, userId.
   - Add MDC population filter.
   - Acceptance: Logs are JSON and contain correlation IDs.

6. Global exception mapping & audit logs
   - Enhance error handler to emit audit logs for session creation/deletion.
   - Acceptance: Security-relevant actions produce structured audit entries.

7. Integration tests with Testcontainers
   - Add Testcontainers for Postgres (and Kafka/MinIO if used) to run integration tests in CI.
   - Acceptance: CI runs integration tests in ephemeral containers.

8. Docker Compose for local production-like stack
   - Compose file with Postgres, MinIO (S3), (optionally Kafka) and the service.
   - Files: `docker-compose.yml`
   - Acceptance: `docker-compose up` runs service with dependencies.

9. CI pipeline
   - Add GitHub Actions workflow to build, test, run linters, build Docker image, scan image (Trivy), push to registry on tags.
   - Files: `.github/workflows/ci.yml`
   - Acceptance: CI pipeline succeeds on PRs and main.

10. Security & secret management
   - Add instructions to use HashiCorp Vault or Kubernetes secrets for production; integrate with Spring Cloud Vault if needed.
   - Acceptance: Secrets not in repo; docs show how to provision.

---

PHASE 2 — Scalability, Reliability, Compliance (~2-4 weeks)
Goal: Make system scale beyond single-instance and provide reliability guarantees and compliance features.

Tasks
1. Ingestion architecture (sync vs async)
   - Implement asynchronous ingestion via Kafka if throughput > threshold. Provide Kafka producers in controllers and a consumer that persists messages to DB.
   - Provide schema for Kafka message (Protobuf/JSON) and versioning strategy.
   - Files: `ingest/kafka/ProducerConfig`, `ingest/kafka/Consumer`.
   - Acceptance: App can accept messages and persist via consumer; consumer lag monitored.

2. Attachments & object storage
   - Implement presigned URL endpoints for uploads (MinIO/S3), store metadata in DB and enforce size limits.
   - Files: `storage/S3Service.java`, `api/upload/*`
   - Acceptance: Upload flow works locally with MinIO; metadata is persisted.

3. Retention, archival, deletion workflow
   - Scheduled job to move expired messages to S3 archives (or soft-delete then purge after grace period). Implement consistent deletion with cascading deletes for session removal.
   - Files: `jobs/RetentionJob.java`
   - Acceptance: Messages older than retention move to archive and removed from primary DB as configured.

4. Observability
   - Integrate Micrometer with Prometheus, and OpenTelemetry tracing. Expose `/actuator/prometheus` and ensure metrics like request latencies, DB times, Kafka lag appear.
   - Add example Grafana dashboards.
   - Acceptance: Metrics and traces available in staging.

5. Backups & DR
   - Automate DB backups (managed snapshots) and test restore procedure. For object storage, enable versioning & lifecycle.
   - Acceptance: Recovery procedure documented & tested.

6. Load testing & performance tuning
   - Create k6 scripts to simulate message ingestion patterns (per-session bursty writes). Run in staged environment and tune DB indexes, connection pool, and Kafka partitions.
   - Acceptance: System meets target throughput and latency goals.

7. Compliance
   - Implement encryption-at-rest (DB and S3), access logs, and configurable retention per-tenant.
   - Add feature flags for PII redaction if needed.

---

PHASE 3 — Deployment & Runbook (~1-2 weeks)
Goal: Automated, repeatable deployment to Kubernetes with monitoring, alerts, and runbooks.

Tasks
1. Helm chart and Kubernetes manifests
   - Create Helm chart with configurable values (replica count, resources, probes, env secrets reference).
   - Files: `charts/chat-storage/*`
   - Acceptance: `helm install` deploys app to cluster with values override.

2. Ingress, TLS, and API gateway
   - Configure Ingress (NGINX/Contour) and TLS certs (cert-manager). Add rate limiting at API gateway too.
   - Acceptance: Secure endpoint working under TLS.

3. Canary & rollbacks
   - Implement Helm-based canary or use service mesh (Istio) to do traffic shifting and automated rollback on errors.
   - Acceptance: New versions can be rolled back quickly.

4. Alerts & runbooks
   - Add Prometheus alerts for DB latency, Kafka consumer lag, error rate, and pager/runbook steps for on-call.
   - Acceptance: Runbooks are in repo and tested with fire drills.

5. Security audit & scanning
   - Run dependency scans, container image vulnerabilities, static analysis. Fix critical issues before production.
   - Acceptance: No critical CVEs present or a mitigation plan.

---

Cross-cutting tasks (applies across phases)
- API OpenAPI/Swagger: maintain complete documentation; version the API (v1 -> v2) and provide migration notes.
- Idempotency: support `Idempotency-Key` header for POSTs to avoid duplicates.
- Observability correlation: add request ID header (`X-Request-ID`) and include it in logs & traces.
- Internationalization/timezone: store timestamps in UTC and expose ISO-8601.
- Pagination: cursor-based pagination for message lists.
- Testing strategy: unit, integration, contract tests, and performance tests. Include test coverage thresholds.
- Accessibility: ensure API error messages are clear but do not leak internals.

Security checklist (must-have before prod)
- API key or OAuth validated by API gateway
- TLS enabled at ingress
- Secrets not in repo, use Vault/K8s secrets
- Rate limiting configured centrally and per-client
- Audit trail for session deletes and access
- Backups and tested restore

Deliverables (code + infra)
- Source code for service modules (api, service, persistence, ingest, jobs)
- Flyway migrations and sample data scripts
- Dockerfile and `docker-compose.yml`
- Helm chart and Kubernetes manifests
- CI workflows (`.github/workflows/*`)
- README, runbook, and architecture docs
- OpenAPI (swagger) docs

Estimates & Prioritization
- Phase 0 (MVP): 2 days
- Phase 1 (Hardening): 1-2 weeks
- Phase 2 (Scale/Reliability): 2-4 weeks
- Phase 3 (Deployment & Runbook): 1-2 weeks

Notes & Assumptions (confirm before implementation)
- Default retention: 90 days (configurable)
- Default ingestion mode: synchronous for MVP; async via Kafka added in Phase 2 for high throughput
- Storage: Postgres for metadata; S3 for attachments/archives
- Authentication: API Key validated in-service for simplicity; recommend API Gateway for production (JWT/OAuth)

Next concrete actions I will take (unless you change scope)
- Implement Phase 0 server flows (Session + Message entities, controllers, DTOs, validation, global error handler)
- Add API key filter and a small in-memory rate limiter for quick protection
- Add Flyway migration (V1) and Dockerfile + docker-compose for Postgres + service for local testing

---

How to run locally (quick)
1. Build and run service (Gradle wrapper from repo root):

```bash
./gradlew -p chat-storage-system bootRun
```

2. Open Swagger UI:

- http://localhost:8080/swagger-ui.html

3. Example create session & post message (replace <API_KEY> and <sessionId>):

```bash
curl -X POST "http://localhost:8080/api/v1/sessions" -H "Content-Type: application/json" -d '{"userId":"00000000-0000-0000-0000-000000000001","title":"Test session"}'

curl -X POST "http://localhost:8080/api/v1/sessions/<sessionId>/messages" -H "Content-Type: application/json" -d '{"senderId":"00000000-0000-0000-0000-000000000001","role":"user","content":"Hi"}'
```

If you want, I can now start implementing Phase 0 tasks right away (create entities, controllers, validations, error handler, idempotency support). Reply with `go` and I'll scaffold the code changes and tests incrementally and run the build/tests after each step.

# 13. Kafka & Messaging

## What is Kafka?
Apache Kafka is a distributed event streaming platform used for building real-time data pipelines and streaming applications. It is designed for high-throughput, fault-tolerant, scalable, and durable messaging between systems or microservices.

---

## Core Concepts (Crystal Clear Explanations)

### 1. Producer
- **Definition:** A client application that sends (publishes) messages (records) to Kafka topics.
- **How it works:** Producers choose which topic (and optionally partition) to send each message to. They can send messages synchronously or asynchronously.
- **Key Configs:** `acks` (acknowledgment level), `retries`, `batch.size`, `linger.ms`.
- **Idempotent Producer:** Ensures exactly-once delivery by preventing duplicate messages during retries.

### 2. Consumer
- **Definition:** A client application that reads (subscribes to) messages from Kafka topics.
- **How it works:** Consumers pull messages from topics, track their position using offsets, and can be part of a consumer group for parallel processing.
- **Key Configs:** `group.id`, `auto.offset.reset`, `enable.auto.commit`.
- **Manual Offset Commit:** Allows precise control over when a message is considered processed.

### 3. Topic
- **Definition:** A logical channel or category to which records are sent by producers and from which records are read by consumers.
- **Properties:** Topics are split into partitions for scalability and parallelism.
- **Retention:** Messages are retained for a configurable period (default 7 days), regardless of consumption.

### 4. Partition
- **Definition:** A topic is divided into one or more partitions, each being an ordered, immutable sequence of records.
- **Why partitions?**
    - Enable parallelism (multiple consumers can read from different partitions).
    - Provide scalability and higher throughput.
    - Each partition is replicated for fault tolerance.

### 5. Offset
- **Definition:** A unique, sequential number assigned to each record within a partition.
- **Usage:** Consumers use offsets to keep track of which messages have been read.
- **Offset Management:** Can be automatic (auto-commit) or manual (explicit commit after processing).

### 6. Consumer Group
- **Definition:** A group of consumers that work together to consume messages from a topic.
- **How it works:** Each partition is assigned to only one consumer in the group at a time, enabling parallel processing and load balancing.
- **Rebalancing:** When consumers join or leave, partitions are reassigned.

### 7. Delivery Semantics
- **At-most-once:** Messages may be lost but are never redelivered.
- **At-least-once:** Messages are never lost but may be redelivered (duplicates possible).
- **Exactly-once:** Each message is delivered once and only once (requires idempotent producer and transactional consumer).

### 8. Broker
- **Definition:** A Kafka server that stores data and serves client requests.
- **Cluster:** Multiple brokers form a Kafka cluster, sharing data and load.

### 9. Zookeeper (Legacy, but still common)
- **Role:** Coordinates brokers, manages cluster metadata, leader election, and configuration. (Kafka is moving toward removing Zookeeper dependency.)

### 10. Replication & Fault Tolerance
- **Replication:** Each partition can have multiple replicas (one leader, others are followers).
- **Leader Election:** Only the leader handles reads/writes; followers replicate data for durability.
- **ISR (In-Sync Replicas):** Replicas that are fully caught up with the leader.

### 11. Message Ordering
- **Guarantee:** Kafka guarantees ordering only within a partition, not across partitions.
- **Keyed Messages:** Messages with the same key always go to the same partition, preserving order for that key.

### 12. Retention & Compaction
- **Retention:** Messages are kept for a set time or size, even if consumed.
- **Log Compaction:** Keeps only the latest value for each key, useful for change logs and state recovery.

---

## Kafka Producer Example (Java)
```java
Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
KafkaProducer<String, String> producer = new KafkaProducer<>(props);
producer.send(new ProducerRecord<>("my-topic", "key", "value"));
producer.close();
```

## Kafka Consumer Example (Java)
```java
Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("group.id", "my-group");
props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
consumer.subscribe(Arrays.asList("my-topic"));
while (true) {
    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
    for (ConsumerRecord<String, String> record : records) {
        System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
    }
    consumer.commitSync(); // manual offset commit
}
```

## Spring Kafka Example
```java
@Service
public class MyProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    public void send(String msg) {
        kafkaTemplate.send("my-topic", msg);
    }
}

@Service
public class MyConsumer {
    @KafkaListener(topics = "my-topic", groupId = "my-group")
    public void listen(String message) {
        System.out.println("Received: " + message);
    }
}
```

---

## Advanced Concepts

### Transactions in Kafka
- **Transactional Producer:** Guarantees exactly-once semantics across multiple partitions and topics.
- **How:** Use `producer.initTransactions()`, `beginTransaction()`, `commitTransaction()`, `abortTransaction()`.
- **Use case:** Financial systems, event-driven workflows.

### Schema Registry
- **Purpose:** Manages Avro/Protobuf/JSON schemas for message validation and evolution.
- **Benefits:** Ensures producers and consumers agree on message structure, enables schema evolution.

### Kafka Streams & KSQL
- **Kafka Streams:** Java library for building stream processing apps on top of Kafka.
- **KSQL:** SQL-like language for querying and processing Kafka topics in real time.

### Monitoring & Lag
- **Consumer Lag:** Difference between the latest offset and the consumer's committed offset. Indicates if consumers are keeping up.
- **Tools:** Kafka Manager, Confluent Control Center, Burrow, Prometheus, Grafana.

---

## Best Practices
- Use partitions for scalability and parallelism.
- Set appropriate replication factor for durability.
- Monitor consumer lag and broker health.
- Use idempotent and transactional producers for exactly-once delivery.
- Handle offset commits carefully to avoid data loss or duplication.
- Secure Kafka with SSL, SASL, and ACLs.
- Use schema registry for message compatibility.
- Tune batch size, linger.ms, and compression for performance.

---

## Common Interview Questions (with Sample Answers)
- **How does Kafka ensure durability and fault tolerance?**
    - By replicating partitions across multiple brokers, using leader/follower model, and persisting messages to disk.
- **What is the difference between a topic and a partition?**
    - A topic is a logical channel; a partition is a unit of parallelism and storage within a topic.
- **How do consumer groups work?**
    - Each partition is assigned to only one consumer in a group at a time, enabling parallel processing and load balancing.
- **How do you achieve exactly-once delivery?**
    - Use idempotent and transactional producers, and commit offsets only after successful processing.
- **What is the role of Zookeeper in Kafka?**
    - Manages broker metadata, leader election, and configuration (being phased out in newer Kafka versions).
- **How do you handle message ordering?**
    - Kafka guarantees order within a partition; use message keys to ensure related messages go to the same partition.
- **What is log compaction?**
    - A feature that retains only the latest value for each key in a topic, useful for change logs and state recovery.
- **What is consumer lag and why is it important?**
    - The gap between the latest message and the consumer's committed offset; high lag means consumers are falling behind.
- **How do you secure a Kafka cluster?**
    - Use SSL/TLS for encryption, SASL for authentication, and ACLs for authorization.
- **What is the difference between at-least-once and exactly-once delivery?**
    - At-least-once may deliver duplicates; exactly-once guarantees no duplicates and no data loss.

---

## Practical Use Case Based Interview Questions & Answers

### Q: How would you design a real-time order tracking system using Kafka?
**A:**
- Use Kafka topics to stream order events (created, shipped, delivered).
- Producers (order service, shipping service) publish events to topics.
- Consumers (tracking UI, analytics) subscribe to topics for real-time updates.
- Use message keys (order ID) to ensure all events for an order go to the same partition (preserving order).
- Use Kafka Streams for aggregating and joining events (e.g., enrich order with shipment info).
- Ensure durability and replayability by setting appropriate retention and using compacted topics for latest order status.

### Q: How do you implement event-driven microservices communication with Kafka?
**A:**
- Each microservice publishes domain events to Kafka topics when state changes.
- Other services subscribe to relevant topics to react to events (e.g., payment service listens to order-created events).
- Use consumer groups for scaling consumers.
- Use schema registry to ensure message compatibility.
- Handle failures by retrying or using dead-letter topics.

### Q: How would you guarantee message processing exactly once in a financial transaction system?
**A:**
- Use idempotent and transactional producers to avoid duplicate messages.
- Use transactional consumer logic: process the message and commit the offset in the same transaction.
- Store processing results and offsets atomically (e.g., in the same database transaction).
- Use Kafka's `enable.idempotence=true` and `transactional.id` configs.

### Q: How do you handle schema evolution in a Kafka-based system?
**A:**
- Use a schema registry to manage Avro/Protobuf/JSON schemas.
- Register new schema versions and ensure backward/forward compatibility.
- Consumers and producers negotiate schema versions at runtime.
- Use compatibility checks to prevent breaking changes.

### Q: How would you implement a retry mechanism for failed message processing?
**A:**
- On failure, do not commit the offset; the message will be retried.
- For controlled retries, use a retry topic (dead-letter topic pattern):
    - On failure, produce the message to a retry topic with a delay.
    - After max retries, send to a dead-letter topic for manual inspection.
- Use frameworks like Spring Kafka's error handling and retry features.

### Q: How do you ensure data consistency between Kafka and a database?
**A:**
- Use the "transactional outbox" pattern: write the event and DB update in the same transaction, then publish the event from the outbox table.
- Alternatively, use Kafka Connect with CDC (Change Data Capture) to stream DB changes to Kafka.
- For consuming, process the message and DB update in a single transaction, then commit the offset.

### Q: How would you monitor and alert on consumer lag in production?
**A:**
- Use Kafka metrics (JMX, Prometheus) to track consumer lag (difference between latest offset and committed offset).
- Set up alerts for high lag using monitoring tools (Grafana, Confluent Control Center).
- Investigate causes: slow consumers, partition imbalance, or broker issues.

### Q: How do you handle message ordering requirements in a multi-partition topic?
**A:**
- Use a message key (e.g., user ID, order ID) to ensure all related messages go to the same partition.
- Design the partitioning strategy to balance load while preserving order for related events.
- If strict global ordering is needed, use a single partition (at the cost of throughput).

### Q: How would you migrate a legacy queue-based system to Kafka?
**A:**
- Build connectors or adapters to read from the legacy queue and produce to Kafka topics.
- Gradually migrate consumers to read from Kafka instead of the old queue.
- Run both systems in parallel during migration, ensuring no data loss.
- Use Kafka Connect or custom bridge services for integration.

### Q: How do you secure sensitive data in Kafka messages?
**A:**
- Encrypt sensitive fields at the application level before sending to Kafka.
- Use SSL/TLS for encryption in transit.
- Use SASL for authentication and ACLs for authorization.
- Mask or redact sensitive data in logs and monitoring tools.

---

## Further Reading & Tools
- [Kafka Official Documentation](https://kafka.apache.org/documentation/)
- [Confluent Kafka Tutorials](https://developer.confluent.io/learn-kafka/)
- [Spring Kafka Reference](https://docs.spring.io/spring-kafka/docs/current/reference/html/)
- [Kafka Streams Documentation](https://kafka.apache.org/documentation/streams/)
- [KSQL Documentation](https://ksqldb.io/)

---

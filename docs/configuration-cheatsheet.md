# Kafka Configuration Cheatsheet

## Broker Configuration

### Performance Tuning
num.network.threads=8
num.io.threads=16
socket.send.buffer.bytes=102400
socket.receive.buffer.bytes=102400

### Reliability
default.replication.factor=3
min.insync.replicas=2
unclean.leader.election.enable=false

## Producer Configuration (application.yml)

### High Throughput
spring:
  kafka:
    producer:
      batch-size: 65536
      linger-ms: 5
      compression-type: snappy
      bootstrap-servers: localhost:9092

### Low Latency  
spring:
  kafka:
    producer:
      batch-size: 0
      linger-ms: 0
      bootstrap-servers: localhost:9092

## Consumer Configuration (application.yml)

spring:
  kafka:
    consumer:
      enable-auto-commit: false
      isolation-level: read_committed
      bootstrap-servers: localhost:9092
      group-id: my-consumer-group

## Maven Properties (pom.xml)

<properties>
    <java.version>21</java.version>
    <spring-kafka.version>3.3.0</spring-kafka.version>
    <testcontainers.version>1.20.1</testcontainers.version>
</properties>

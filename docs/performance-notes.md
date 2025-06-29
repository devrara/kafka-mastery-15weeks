# Kafka Performance Notes

## Baseline Metrics

| Scenario | Throughput | Latency P99 | Memory | Maven Build Time |
|----------|------------|-------------|---------|------------------|
| Basic Producer | TBD | TBD | TBD | TBD |
| Basic Consumer | TBD | TBD | TBD | TBD |
| Kafka Streams | TBD | TBD | TBD | TBD |

## Optimization Findings

### Week 2: Configuration Tuning
- Finding: Increasing batch.size from 16384 to 65536 improved throughput by X%
- Trade-off: Latency increased by Xms
- Recommendation: Use for high-throughput scenarios

### Week 11: Advanced Optimization
- Finding: TBD
- Trade-off: TBD
- Recommendation: TBD

## Maven Performance Tips

# Parallel builds (use number of CPU cores)
mvn -T 4 clean compile

# Skip tests for faster builds during development
mvn clean package -DskipTests

# Offline mode (faster dependency resolution)
mvn -o spring-boot:run

# Use local repository for faster builds
mvn install -DskipTests

# Daemon mode (if using Maven 3.9+)
mvnd clean compile  # Maven Daemon - faster builds

## Spring Boot Performance Tips

### application.yml optimizations:
spring:
  jpa:
    hibernate:
      ddl-auto: none  # Don't auto-create schema in production
  kafka:
    producer:
      batch-size: 65536
      linger-ms: 5
      buffer-memory: 33554432
    consumer:
      fetch-min-size: 1024
      fetch-max-wait: 500

### JVM Options
# Set via environment variable
$env:MAVEN_OPTS = "-Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Or in mvn command
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx2g -Xms1g"

## Windows Specific Optimizations

### Disk I/O
- Use SSD for better performance
- Exclude project folders from Windows Defender real-time scanning
- Use Windows Terminal instead of CMD for better performance

### Memory
- Monitor with Task Manager or Resource Monitor
- Use Windows Performance Monitor for detailed metrics

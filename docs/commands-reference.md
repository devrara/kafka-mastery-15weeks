# Kafka Commands Reference (Windows/PowerShell)

## Topic Management

# Create topic
kafka-topics.bat --create --topic my-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# List topics
kafka-topics.bat --list --bootstrap-server localhost:9092

# Describe topic
kafka-topics.bat --describe --topic my-topic --bootstrap-server localhost:9092

# Delete topic
kafka-topics.bat --delete --topic my-topic --bootstrap-server localhost:9092

## Producer Commands

# Console producer
kafka-console-producer.bat --topic my-topic --bootstrap-server localhost:9092

# With key
kafka-console-producer.bat --topic my-topic --bootstrap-server localhost:9092 --property "parse.key=true" --property "key.separator=:"

## Consumer Commands

# Console consumer
kafka-console-consumer.bat --topic my-topic --bootstrap-server localhost:9092 --from-beginning

# With group
kafka-console-consumer.bat --topic my-topic --bootstrap-server localhost:9092 --group my-group

## Consumer Groups

# List groups
kafka-consumer-groups.bat --bootstrap-server localhost:9092 --list

# Describe group
kafka-consumer-groups.bat --bootstrap-server localhost:9092 --group my-group --describe

# Reset offsets
kafka-consumer-groups.bat --bootstrap-server localhost:9092 --group my-group --reset-offsets --to-earliest --topic my-topic --execute

## Maven Commands

# Compile project
mvn clean compile

# Run tests
mvn test

# Run Spring Boot app
mvn spring-boot:run

# Package JAR
mvn clean package

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"

# Skip tests during package
mvn clean package -DskipTests

# Parallel build (faster)
mvn -T 4 clean compile

## Docker Commands (for Kafka)

# Start Kafka cluster
docker-compose up -d

# Stop Kafka cluster
docker-compose down

# View logs
docker-compose logs kafka
docker-compose logs zookeeper

# Check running containers
docker ps

# Clean up volumes
docker-compose down -v

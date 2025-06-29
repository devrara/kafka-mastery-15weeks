# Kafka Troubleshooting Guide (Windows)

## Common Issues

### Issue: "Connection refused"
Symptoms: Cannot connect to broker
Solutions:
1. Check if Kafka is running: docker ps
2. Verify ports: netstat -an | findstr 9092
3. Check docker-compose logs: docker-compose logs kafka
4. Verify Docker Desktop is running

### Issue: "Topic already exists"
Symptoms: Error creating topic
Solutions:
1. List existing topics: kafka-topics.bat --list --bootstrap-server localhost:9092
2. Delete if needed: kafka-topics.bat --delete --topic my-topic --bootstrap-server localhost:9092

### Issue: Maven build fails
Symptoms: Maven compilation errors
Solutions:
1. Check Java version: java --version (should be 21+)
2. Clean and compile: mvn clean compile
3. Check dependencies: mvn dependency:tree
4. Clear Maven cache: mvn dependency:purge-local-repository

### Issue: PowerShell execution policy
Symptoms: Cannot run scripts
Solutions:
1. Check current policy: Get-ExecutionPolicy
2. Set policy: Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

### Issue: High memory usage
Symptoms: OutOfMemoryError
Solutions:
1. Adjust JVM settings via MAVEN_OPTS environment variable
   MAVEN_OPTS=-Xmx1024m -Xms512m

### Issue: Port already in use
Symptoms: "Port 8080 is already in use"
Solutions:
1. Find process using port: netstat -ano | findstr :8080
2. Kill process: taskkill /PID <PID> /F
3. Or change port in application.yml: server.port=8081

## Monitoring Commands (Windows)

# Check disk usage
Get-WmiObject -Class Win32_LogicalDisk | Select-Object Size,FreeSpace,DeviceID

# Network connections
netstat -an | findstr 9092

# Java processes
jps -l

# Check running services
Get-Service | Where-Object {$_.Status -eq "Running"}

# Check system resources
Get-Process | Sort-Object CPU -Descending | Select-Object -First 10

## Windows-specific Notes

### Environment Variables
# Set JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"

# Add to PATH
$env:PATH += ";C:\Program Files\Java\jdk-21\bin"

# Set Maven options
$env:MAVEN_OPTS = "-Xmx1024m -Xms512m"

### File Paths
- Use backslashes: projects\week-01\
- Or forward slashes work too: projects/week-01/
- Avoid spaces in project names

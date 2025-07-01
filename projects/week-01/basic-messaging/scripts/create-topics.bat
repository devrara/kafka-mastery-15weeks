REM =======================================================================
REM create-topics.bat - Script para crear topics básicos con Docker
REM =======================================================================
@echo off
echo.
echo ========================================
echo    Creando Topics de Kafka (Docker)
echo ========================================
echo.

REM Configuración
set BOOTSTRAP_SERVERS=localhost:9092
set DOCKER_CONTAINER_NAME=basic-messaging-kafka-1

REM Verificar si Docker está disponible
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker no está instalado o no está en el PATH
    echo Por favor instala Docker e intenta de nuevo
    pause
    exit /b 1
)

REM Verificar si Kafka está disponible
echo Verificando conexión a Kafka...
docker exec %DOCKER_CONTAINER_NAME% kafka-topics --bootstrap-server %BOOTSTRAP_SERVERS% --list >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: No se puede conectar a Kafka en %BOOTSTRAP_SERVERS%
    echo Posibles causas:
    echo 1. El contenedor de Kafka no está ejecutándose
    echo 2. El nombre del contenedor no es '%DOCKER_CONTAINER_NAME%'
    echo.
    echo Contenedores Docker ejecutándose:
    docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    echo.
    echo Si tu contenedor tiene otro nombre, edita este script y cambia DOCKER_CONTAINER_NAME
    pause
    exit /b 1
)

echo Kafka está disponible. Creando topics...
echo.

REM Topic básico para SimpleProducer/SimpleConsumer (coincide con KafkaConfig.TOPIC_NAME)
echo Creando topic 'my-first-topic'...
docker exec %DOCKER_CONTAINER_NAME% kafka-topics ^
    --bootstrap-server %BOOTSTRAP_SERVERS% ^
    --create ^
    --topic my-first-topic ^
    --partitions 1 ^
    --replication-factor 1 ^
    --if-not-exists

REM Topic para demo de múltiples particiones
echo Creando topic 'multi-partition-topic'...
docker exec %DOCKER_CONTAINER_NAME% kafka-topics ^
    --bootstrap-server %BOOTSTRAP_SERVERS% ^
    --create ^
    --topic multi-partition-topic ^
    --partitions 3 ^
    --replication-factor 1 ^
    --if-not-exists

REM Topic para pruebas generales
echo Creando topic 'test-topic'...
docker exec %DOCKER_CONTAINER_NAME% kafka-topics ^
    --bootstrap-server %BOOTSTRAP_SERVERS% ^
    --create ^
    --topic test-topic ^
    --partitions 2 ^
    --replication-factor 1 ^
    --if-not-exists

echo.
echo ✅ Topics creados exitosamente!
echo.
echo Listando todos los topics:
docker exec %DOCKER_CONTAINER_NAME% kafka-topics --bootstrap-server %BOOTSTRAP_SERVERS% --list

echo.
echo Para ver detalles de un topic específico, usa:
echo docker exec %DOCKER_CONTAINER_NAME% kafka-topics --bootstrap-server %BOOTSTRAP_SERVERS% --describe --topic [TOPIC_NAME]
echo.
echo NOTA: Si tu contenedor de Kafka tiene otro nombre, edita la variable DOCKER_CONTAINER_NAME en este script
echo.
pause
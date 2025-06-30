# 🛠️ Proyecto 1: Setup de Kafka Local

Este proyecto configura un entorno local de Apache Kafka usando Docker Compose, incluyendo Kafka Broker, Zookeeper (o KRaft si se desea) y Kafka UI para visualización y gestión.

## 📦 Componentes

- Apache Kafka 3.5+
- Zookeeper (opcional si usas KRaft)
- Kafka UI (provectuslabs/kafka-ui)

## 🚀 Cómo ejecutarlo

```powershell
# Posiciónate en el proyecto
cd projects\week-01\kafka-setup

# Levantar los servicios
docker-compose up -d

# Verificar los contenedores
docker-compose ps

# Acceder a Kafka UI
http://localhost:8080
```

## 📋 Comandos útiles para configurar Topics

### Crear Topics

```powershell
# Reemplaza [container-name] con el nombre real de tu contenedor Kafka

# Crear un topic básico
docker exec -it [container-name] kafka-topics --create --topic mi-primer-topic --bootstrap-server localhost:9092

# Crear topic con configuración específica
docker exec -it [container-name] kafka-topics --create `
  --topic eventos-usuario `
  --bootstrap-server localhost:9092 `
  --partitions 3 `
  --replication-factor 1 `
  --config retention.ms=604800000 `
  --config segment.ms=86400000

# Crear topic para logs con compactación
docker exec -it [container-name] kafka-topics --create `
  --topic user-profiles `
  --bootstrap-server localhost:9092 `
  --partitions 6 `
  --replication-factor 1 `
  --config cleanup.policy=compact `
  --config min.cleanable.dirty.ratio=0.1
```

### Listar y describir Topics

```powershell
# Reemplaza [container-name] con el nombre real de tu contenedor Kafka

# Listar todos los topics
docker exec -it [container-name] kafka-topics --list --bootstrap-server localhost:9092

# Describir un topic específico
docker exec -it [container-name] kafka-topics --describe --topic mi-primer-topic --bootstrap-server localhost:9092

# Describir todos los topics
docker exec -it [container-name] kafka-topics --describe --bootstrap-server localhost:9092
```

### Modificar Topics existentes

```powershell
# Reemplaza [container-name] con el nombre real de tu contenedor Kafka

# Aumentar particiones (no se pueden reducir)
docker exec -it [container-name] kafka-topics --alter `
  --topic mi-primer-topic `
  --partitions 5 `
  --bootstrap-server localhost:9092

# Modificar configuración de un topic
docker exec -it [container-name] kafka-configs --alter `
  --entity-type topics `
  --entity-name mi-primer-topic `
  --add-config retention.ms=259200000 `
  --bootstrap-server localhost:9092
```

## 🔧 Configuraciones de Topics recomendadas

### Topic para eventos de alta frecuencia
```powershell
# Reemplaza [container-name] con el nombre real de tu contenedor Kafka

docker exec -it [container-name] kafka-topics --create `
  --topic eventos-alta-frecuencia `
  --bootstrap-server localhost:9092 `
  --partitions 12 `
  --replication-factor 1 `
  --config retention.ms=86400000 `
  --config segment.ms=3600000 `
  --config compression.type=lz4
```

### Topic para datos de configuración (compactado)
```powershell
# Reemplaza [container-name] con el nombre real de tu contenedor Kafka

docker exec -it [container-name] kafka-topics --create `
  --topic configuracion-app `
  --bootstrap-server localhost:9092 `
  --partitions 1 `
  --replication-factor 1 `
  --config cleanup.policy=compact `
  --config segment.ms=86400000 `
  --config min.cleanable.dirty.ratio=0.01
```

### Topic para métricas y monitoreo
```powershell
# Reemplaza [container-name] con el nombre real de tu contenedor Kafka

docker exec -it [container-name] kafka-topics --create `
  --topic metricas-sistema `
  --bootstrap-server localhost:9092 `
  --partitions 6 `
  --replication-factor 1 `
  --config retention.ms=2592000000 `
  --config segment.ms=86400000 `
  --config max.message.bytes=1048576
```

## 📊 Comandos para testing con Producer/Consumer

### Producer de consola
```powershell
# Reemplaza [container-name] con el nombre real de tu contenedor Kafka

# Producer básico
docker exec -it [container-name] kafka-console-producer `
  --topic mi-primer-topic `
  --bootstrap-server localhost:9092

# Producer con key
docker exec -it [container-name] kafka-console-producer `
  --topic user-profiles `
  --bootstrap-server localhost:9092 `
  --property "parse.key=true" `
  --property "key.separator=:"
```

### Consumer de consola
```powershell
# Reemplaza [container-name] con el nombre real de tu contenedor Kafka

# Consumer básico (desde el final)
docker exec -it [container-name] kafka-console-consumer `
  --topic mi-primer-topic `
  --bootstrap-server localhost:9092

# Consumer desde el principio
docker exec -it [container-name] kafka-console-consumer `
  --topic mi-primer-topic `
  --bootstrap-server localhost:9092 `
  --from-beginning

# Consumer con grupo de consumidores
docker exec -it [container-name] kafka-console-consumer `
  --topic eventos-usuario `
  --bootstrap-server localhost:9092 `
  --group mi-grupo-consumidores `
  --from-beginning

# Consumer mostrando keys y particiones
docker exec -it [container-name] kafka-console-consumer `
  --topic user-profiles `
  --bootstrap-server localhost:9092 `
  --property print.key=true `
  --property print.partition=true `
  --property print.offset=true `
  --from-beginning
```

## 🎯 Script de inicialización de Topics

Puedes crear un script **init-topics.ps1** para automatizar la creación de topics:

```powershell
Write-Host "🚀 Creando topics iniciales..." -ForegroundColor Green

# Topic para eventos de usuario
docker exec [container-name] kafka-topics --create `
  --topic user-events `
  --bootstrap-server localhost:9092 `
  --partitions 3 `
  --replication-factor 1 `
  --config retention.ms=604800000 `
  --if-not-exists

# Topic para órdenes
docker exec [container-name] kafka-topics --create `
  --topic orders `
  --bootstrap-server localhost:9092 `
  --partitions 6 `
  --replication-factor 1 `
  --config retention.ms=2592000000 `
  --if-not-exists

# Topic para notificaciones
docker exec [container-name] kafka-topics --create `
  --topic notifications `
  --bootstrap-server localhost:9092 `
  --partitions 2 `
  --replication-factor 1 `
  --config retention.ms=259200000 `
  --if-not-exists

Write-Host "✅ Topics creados exitosamente" -ForegroundColor Green
Write-Host "🌐 Accede a Kafka UI en http://localhost:8080" -ForegroundColor Cyan
```

**Para ejecutar el script:**
```powershell
# Dar permisos de ejecución (si es necesario)
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Ejecutar el script
.\init-topics.ps1
```

## 🖥️ Características de Kafka UI

Una vez que tengas los topics creados, en Kafka UI podrás visualizar:

- **Topics**: Lista de todos los topics con sus configuraciones
- **Particiones**: Distribución de particiones y sus offsets
- **Mensajes**: Explorar mensajes en tiempo real
- **Grupos de consumidores**: Estado y lag de consumer groups
- **Configuraciones**: Modificar settings de topics y brokers
- **Métricas**: Throughput, latencia y otras métricas importantes

## 🔍 Comandos de monitoreo

```powershell
# Ver grupos de consumidores
docker exec -it [container-name] kafka-consumer-groups --list --bootstrap-server localhost:9092

# Describir un grupo específico
docker exec -it [container-name] kafka-consumer-groups `
  --describe `
  --group mi-grupo-consumidores `
  --bootstrap-server localhost:9092

# Ver configuración del broker
docker exec -it [container-name] kafka-configs `
  --describe `
  --entity-type brokers `
  --entity-name 1 `
  --bootstrap-server localhost:9092
```

## 🧹 Comandos de limpieza

```powershell
# Eliminar un topic
docker exec -it [container-name] kafka-topics --delete --topic mi-topic-temporal --bootstrap-server localhost:9092

# Parar todos los servicios
docker-compose down

# Parar y eliminar volúmenes (⚠️ elimina todos los datos)
docker-compose down -v
```

## 💡 Tips para usar con Kafka UI

1. **Explora los topics**: Usa la pestaña "Topics" para ver configuraciones y métricas
2. **Envía mensajes de prueba**: Utiliza la función "Produce Message" desde la UI
3. **Monitorea consumer groups**: Revisa el lag y estado en "Consumer Groups"
4. **Configura alertas**: Aprovecha las métricas para monitoring
5. **Exporta configuraciones**: Kafka UI permite exportar configuraciones de topics

¡Ahora tienes un entorno completo de Kafka con interfaz gráfica para experimentar y aprender! 🎉


# Proyecto 2: Sistema Básico de Mensajería con Kafka

## 📋 Descripción

Este proyecto implementa un sistema básico de mensajería usando Apache Kafka. Incluye ejemplos de producer, consumer y una demostración avanzada del particionado en Kafka.

## 🎯 Objetivos de Aprendizaje

- Comprender los conceptos básicos de Kafka (topics, particiones, mensajes)
- Implementar un producer simple
- Implementar un consumer simple
- Explorar diferentes estrategias de particionado
- Configurar topics con múltiples particiones
- Usar Kafka Admin Client para gestión de topics

## 🏗️ Estructura del Proyecto

```
basic-messaging/
├── docker-compose.yml                     # Contenedores para Kafka, Zookeeper y Kafka UI
├── pom.xml                                # Configuración Maven
├── README.md                              # Este archivo
├── scripts/                               # Scripts de ejecución
│   ├── run-producer.bat                   # Ejecutar producer
│   ├── run-consumer.bat                   # Ejecutar consumer
│   ├── run-multi-partition-demo.bat       # Ejecutar demo particiones
│   └── create-topics.bat                  # Crear topics
└── src/
    └── main/
        ├── java/edu/devra/kafka/
        │   ├── config/
        │   │   └── KafkaConfig.java        # Configuración centralizada
        │   ├── producer/
        │   │   └── SimpleProducer.java     # Producer básico
        │   ├── consumer/
        │   │   └── SimpleConsumer.java     # Consumer básico
        │   └── demo/
        │       └── MultiPartitionDemo.java # Demo de particionado
        └── resources/
            └── logback.xml                 # Configuración de logs
```

## 🔧 Configuración Previa

### Requisitos
- Java **21**
- Maven 3.6 o superior
- Docker y Docker Compose
- Apache Kafka ejecutándose en contenedor Docker en `localhost:9092`
- Kafka UI (opcional) en `http://localhost:8080`


**Nota**: Si tu contenedor de Kafka tiene otro nombre, ajusta la variable `DOCKER_CONTAINER_NAME` en el script `create-topics.bat`.

---

## 🧭 Flujo de Trabajo Recomendado (PowerShell)

```powershell
# Paso 1: Levantar Kafka, Zookeeper y Kafka UI
docker-compose up -d

# Verificar que el contenedor de Kafka esté corriendo
docker ps

# Verificar que Kafka esté respondiendo
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list

# Paso 2: Compilar el proyecto
mvn clean install

# Paso 3: Crear los topics
# Ajustar la variable DOCKER_CONTAINER_NAME del script al nombre del contenedor de kafka que se está ejecutando en Docker

.\scripts\create-topics.bat

# (o hacerlo manualmente desde Kafka UI: http://localhost:8080)

# Paso 4: Ejecutar el Consumer (ventana 1)
mvn clean compile exec:java --% -Dexec.mainClass="edu.devra.kafka.consumer.SimpleConsumer"

# Paso 5: Ejecutar el Producer (ventana 2)
mvn clean compile exec:java --% -Dexec.mainClass="edu.devra.kafka.producer.SimpleProducer"

# Paso 6: Ejecutar la demo de particionado (opcional - ventana 3)
mvn clean compile exec:java --% -Dexec.mainClass="edu.devra.kafka.demo.SimpleMultiPartitionDemo"

```

---

## 📊 Componentes del Sistema

### 1. SimpleProducer
- **Función**: Envía mensajes interactivos al topic `my-first-topic`
- **Características**:
  - Interfaz de línea de comandos
  - Envío de mensajes personalizados
  - Manejo de errores y callbacks
  - Timestamps automáticos

**Uso**:
```
Ejecutar → Escribir mensajes → Enter para enviar → 'exit' para salir
```

### 2. SimpleConsumer
- **Función**: Consume mensajes del topic `my-first-topic`
- **Características**:
  - Polling continuo
  - Muestra información detallada (partición, offset, timestamp)
  - Manejo de errores
  - Commit manual de offsets

**Salida esperada**:
```
📨 Mensaje recibido:
   Topic: my-first-topic | Partición: 0 | Offset: 42
   Key: null | Timestamp: 2024-01-15T10:30:00.000Z
   Valor: Hola desde el producer!
```

### 3. MultiPartitionDemo
- **Función**: Demuestra diferentes estrategias de particionado
- **Características**:
  - Crea topic automáticamente con 3 particiones
  - Tres demos de particionado:
    1. **Por Key (Hash)**: Misma key → misma partición
    2. **Round-Robin**: Sin key → distribución secuencial
    3. **Manual**: Especificación directa de partición

**Estrategias demostradas**:

| Estrategia   | Key            | Partición              | Uso                        |
|--------------|----------------|------------------------|----------------------------|
| Hash         | "user-alice"   | Calculada por hash     | Garantiza orden por usuario|
| Round-Robin  | null           | Secuencial (0,1,2...)  | Distribución uniforme      |
| Manual       | "manual-key-X" | Especificada           | Control total              |

---

## 🔍 Monitoreo

### Kafka UI
- **URL**: http://localhost:8080
- **Visualización**:
  - Topics y particiones
  - Mensajes por partición
  - Offsets de consumidores
  - Distribución de mensajes

### Comandos CLI (PowerShell)
```powershell
# Listar topics
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list

# Describir un topic
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --describe --topic multi-partition-topic

# Ver mensajes en el topic
docker exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic my-first-topic --from-beginning
```

---

## 🧪 Ejercicios Prácticos

### Ejercicio 1: Particionado por Key
1. Ejecutar `MultiPartitionDemo`
2. Observar en Kafka UI cómo se distribuyen los mensajes
3. Verificar que mensajes con la misma key van a la misma partición

### Ejercicio 2: Consumer Groups
1. Ejecutar múltiples instancias del consumer
2. Observar cómo se reparten las particiones
3. Parar un consumer y ver la rebalance

### Ejercicio 3: Producción Manual
1. Usar `SimpleProducer` para enviar mensajes
2. Consumir con `SimpleConsumer`
3. Verificar orden y persistencia

---

## 🐛 Troubleshooting

### Problema: "Connection refused"
**Solución**: Verificar que Kafka esté ejecutándose en el puerto 9092

### Problema: "Topic does not exist"
**Solución**: Ejecutar `scripts\create-topics.bat` o crear el topic desde Kafka UI

### Problema: "No se muestra en Kafka UI"
**Solución**: Verificar que Kafka UI esté en puerto 8080 y actualizar la página

### Problema: Mensajes no se consumen
**Solución**: Verificar que consumer y producer usen el mismo topic

---

## 📚 Conceptos Clave Aprendidos

- **Topic**: Canal de comunicación para mensajes
- **Partición**: División lógica del topic para escalabilidad
- **Offset**: Posición única del mensaje en una partición
- **Key**: Determinante de partición para garantizar orden
- **Producer**: Componente que envía mensajes
- **Consumer**: Componente que lee mensajes
- **Admin Client**: Herramienta para gestión de topics

---

## 📈 Próximos Pasos

1. **Semana 2**: Consumer Groups y Rebalancing
2. **Semana 3**: Serialización y Schemas
3. **Semana 4**: Transacciones y Exactamente-Una-Vez
4. **Semana 5**: Stream Processing con Kafka Streams

---

## 🆘 Soporte

Si encuentras problemas:
1. Revisar logs en consola
2. Verificar configuración en `KafkaConfig.java`
3. Consultar Kafka UI para estado del cluster
4. Revisar que todos los puertos estén disponibles

---

**¡Felicitaciones!** 🎉 Has completado tu primer sistema de mensajería con Kafka usando PowerShell, Docker y Java 21.


# Kafka Semana 1 - Proyecto 3: Chat en Tiempo Real Básico

Este proyecto implementa un sistema de chat en tiempo real utilizando Apache Kafka. Los mensajes de chat son producidos y consumidos a través de tópicos de Kafka, permitiendo una comunicación asíncrona y escalable.

## 🎯 Objetivos del Proyecto

* Entender cómo modelar un mensaje de chat para Kafka.
* Implementar un Producer para enviar mensajes de chat.
* Implementar un Consumer para recibir y mostrar mensajes de chat.
* Crear una aplicación de chat simple utilizando Producer y Consumer de Kafka.

## 📋 Prerequisitos

* **Docker y Docker Compose:** Necesarios para ejecutar Kafka (modo KRaft) localmente.
* **Java Development Kit (JDK) 21 o superior:** Para compilar y ejecutar las aplicaciones Java.
* **Maven:** Herramienta de construcción de proyectos Java.

**Verificar Kafka**
```bash
# Verificar que Kafka esté ejecutándose
docker-compose ps

# Si no está ejecutándose
docker-compose up -d

# Verificar Kafka UI
open http://localhost:8080
```

## 🏗️ Arquitectura del Chat
```
[Usuario A] ──→ ChatProducer ──→ Kafka Topic ──→ ChatConsumer ──→ [Usuario B]
                    ↓                 ↓              ↑
               [chat-messages]    [Particiones]   [Consumer Group]
                                  por sala
```

**Componentes principales:**

1. ChatMessage: Modelo de datos (JSON)
2. ChatProducer: Envía mensajes al topic
3. ChatConsumer: Recibe mensajes del topic
4. ChatApplication: Aplicación principal (CLI)


## ▶️ Ejecución del Proyecto

Abre varias terminales PowerShell para simular diferentes usuarios en el chat.

### 1\. Compilar el Proyecto

Desde la raíz de tu proyecto Kafka (donde está `pom.xml`), ejecuta:

```powershell
mvn clean compile
```

### 2\. Ejecutar la Aplicación de Chat

Ejecuta la aplicación de chat en diferentes terminales PowerShell, especificando un nombre de usuario y una sala.

**Terminal 1 (Usuario Alice en `room1`):**

```powershell
mvn exec:java --% -Dexec.mainClass="edu.devra.kafka.chat.ChatApplication" -Dexec.args="Alice room1"
```

**Terminal 2 (Usuario Bob en `room1`):**

```powershell
mvn exec:java --% -Dexec.mainClass="edu.devra.kafka.chat.ChatApplication" -Dexec.args="Bob room1"
```

**Terminal 3 (Usuario Charlie en `room2` - para probar salas diferentes):**

```powershell
mvn exec:java --% -Dexec.mainClass="edu.devra.kafka.chat.ChatApplication" -Dexec.args="Charlie room2"
```

**Terminal 4 (Usuario Peter en `room2` - para probar salas diferentes):**

```powershell
mvn exec:java --% -Dexec.mainClass="edu.devra.kafka.chat.ChatApplication" -Dexec.args="Peter room2"
```

### 3\. Interacción en el Chat

  * Escribe mensajes en cualquiera de las terminales de chat y presiona Enter.
  * Observa cómo los mensajes aparecen en las otras terminales conectadas a la misma `room`.
  * Los mensajes enviados a `room1` solo serán visibles para los usuarios en `room1`, y lo mismo para `room2`.
  * Para salir de una sesión de chat, escribe `/exit` y presiona Enter.

## 📋 Comandos Útiles de Kafka CLI (Opcional)

Puedes usar la CLI de Kafka para inspeccionar el tópico de chat.

### Crear el tópico `chat-messages` (si no se crea automáticamente al producir el primer mensaje)

Aunque el producer de Kafka puede crear el tópico automáticamente, es buena práctica crearlo manualmente con las particiones deseadas. Para un chat, podrías considerar múltiples particiones si esperas un alto volumen de mensajes o múltiples salas. Aquí usaremos el valor por defecto que crearía Kafka al producir el mensaje, pero si quieres especificar particiones de antemano:

```powershell
docker exec real-chat-kafka kafka-topics --bootstrap-server localhost:9092 --create --topic chat-messages --partitions 3 --replication-factor 1
```

### Listar tópicos para verificar `chat-messages`

```powershell
docker exec real-chat-kafka kafka-topics --bootstrap-server localhost:9092 --list
```

### Describir el tópico `chat-messages`

```powershell
docker exec real-chat-kafka kafka-topics --bootstrap-server localhost:9092 --describe --topic [TOPIC_NAME]
```

### Consumir mensajes directamente desde la consola (para depuración)

```powershell
docker exec -it real-chat-kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic [TOPIC_NAME] --from-beginning
```


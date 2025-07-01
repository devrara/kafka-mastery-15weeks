package edu.devra.kafka.consumer;

import edu.devra.kafka.config.KafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Consumer simple que demuestra el consumo básico de mensajes
 * Incluye manejo de shutdown graceful y commit manual
 */
public class SimpleConsumer {
    private static final Logger logger = LoggerFactory.getLogger(SimpleConsumer.class);
    
    // Formato para mostrar timestamps
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static void main(String[] args) {
        // Obtener group ID de argumentos o usar default
        String groupId = args.length > 0 ? args[0] : "simple-consumer-group";

        System.out.println("Cargando logback.xml desde: " +
                org.slf4j.LoggerFactory.class.getResource("/logback.xml"));

        logger.info("=== Kafka Simple Consumer ===");
        logger.info("Topic: {}", KafkaConfig.TOPIC_NAME);
        logger.info("Group ID: {}", groupId);
        logger.info("Bootstrap Servers: {}", KafkaConfig.BOOTSTRAP_SERVERS);
        logger.info("Esperando mensajes... (Ctrl+C para salir)");
        logger.info("=====================================");
        
        try (var consumer = new KafkaConsumer<String, String>(KafkaConfig.getConsumerProps(groupId))) {
            
            // Configurar shutdown hook para cerrar gracefully
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("🛑 Recibida señal de shutdown, cerrando consumer...");
                consumer.wakeup(); // Interrumpir el poll() actual
            }));
            
            // Suscribirse al topic
            consumer.subscribe(List.of(KafkaConfig.TOPIC_NAME));
            logger.info("✅ Consumer iniciado y suscrito al topic: {}", KafkaConfig.TOPIC_NAME);
            
            int messageCount = 0;
            
            try {
                while (true) {
                    // Poll por nuevos mensajes (timeout de 100ms)
                    var records = consumer.poll(Duration.ofMillis(100));
                    
                    if (records.isEmpty()) {
                        // No hay mensajes, continuar polling
                        continue;
                    }
                    
                    logger.info("📬 Recibidos {} mensajes en este batch", records.count());
                    
                    // Procesar cada mensaje
                    for (ConsumerRecord<String, String> record : records) {
                        messageCount++;
                        processMessage(record, messageCount);
                    }
                    
                    // Commit manual de los offsets después de procesar el batch
                    try {
                        consumer.commitSync();
                        logger.debug("✅ Offsets commiteados exitosamente");
                    } catch (Exception e) {
                        logger.error("❌ Error commiteando offsets", e);
                    }
                }
                
            } catch (WakeupException e) {
                logger.info("🔄 Consumer interrumpido por shutdown hook");
            } catch (Exception e) {
                logger.error("❌ Error inesperado en consumer", e);
            } finally {
                // Commit final de offsets
                try {
                    consumer.commitSync();
                    logger.info("✅ Commit final de offsets realizado");
                } catch (Exception e) {
                    logger.warn("⚠️ Error en commit final", e);
                }
            }
            
        } catch (Exception e) {
            logger.error("❌ Error fatal en consumer", e);
            System.exit(1);
        }
        
        logger.info("🏁 Consumer cerrado correctamente. ¡Hasta luego! 👋");
    }
    
    /**
     * Procesa un mensaje individual y muestra información detallada
     * @param record Mensaje recibido
     * @param messageNumber Número secuencial del mensaje
     */
    private static void processMessage(ConsumerRecord<String, String> record, int messageNumber) {
        try {
            // Convertir timestamp a fecha legible
            LocalDateTime timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(record.timestamp()),
                ZoneId.systemDefault()
            );
            
            // Simular procesamiento (en aplicaciones reales aquí iría la lógica de negocio)
            Thread.sleep(10); // Pequeña pausa para simular procesamiento
            
            logger.info("📨 ========== MENSAJE #{} ==========", messageNumber);
            logger.info("   🔑 Key: {}", record.key() != null ? record.key() : "null");
            logger.info("   💬 Value: {}", record.value());
            logger.info("   📂 Topic: {}", record.topic());
            logger.info("   📍 Partition: {}", record.partition());
            logger.info("   📊 Offset: {}", record.offset());
            logger.info("   🕐 Timestamp: {}", timestamp.format(DATETIME_FORMATTER));
            logger.info("   📏 Value Size: {} bytes", record.serializedValueSize());
            logger.info("   =====================================");
            
            // En una aplicación real, aquí procesarías el mensaje
            // Por ejemplo: guardar en base de datos, enviar email, etc.
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("⚠️ Procesamiento interrumpido");
        } catch (Exception e) {
            logger.error("❌ Error procesando mensaje", e);
            // En producción, podrías enviar a un dead letter queue
        }
    }
}
package edu.devra.kafka.producer;

import edu.devra.kafka.config.KafkaConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Producer simple que permite enviar mensajes interactivamente
 * Demuestra el uso básico del KafkaProducer con callbacks
 */
public class SimpleProducer {
    private static final Logger logger = LoggerFactory.getLogger(SimpleProducer.class);
    
    public static void main(String[] args) {
        logger.info("=== Kafka Simple Producer ===");
        logger.info("Topic: {}", KafkaConfig.TOPIC_NAME);
        logger.info("Bootstrap Servers: {}", KafkaConfig.BOOTSTRAP_SERVERS);
        logger.info("Escribe mensajes (escribe 'exit' para salir):");
        logger.info("=====================================");
        
        // Crear producer con configuración predefinida
        try (var producer = new KafkaProducer<String, String>(KafkaConfig.getProducerProps());
             var scanner = new Scanner(System.in)) {
            
            logger.info("Producer iniciado correctamente");
            
            while (true) {
                System.out.print("> ");
                String message = scanner.nextLine();
                
                // Comando para salir
                if ("exit".equalsIgnoreCase(message.trim())) {
                    logger.info("Saliendo...");
                    break;
                }
                
                // Ignorar mensajes vacíos
                if (message.trim().isEmpty()) {
                    continue;
                }
                
                // Crear key basada en timestamp para distribución
                String key = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
                
                // Crear record
                var record = new ProducerRecord<>(KafkaConfig.TOPIC_NAME, key, message);
                
                // Enviar de forma asíncrona con callback detallado
                producer.send(record, (RecordMetadata metadata, Exception exception) -> {
                    if (exception == null) {
                        // Éxito - mostrar detalles del envío
                        logger.info("✅ Mensaje enviado exitosamente:");
                        logger.info("   📨 Topic: {}", metadata.topic());
                        logger.info("   📂 Partition: {}", metadata.partition());
                        logger.info("   📍 Offset: {}", metadata.offset());
                        logger.info("   🕐 Timestamp: {}", 
                                  LocalDateTime.ofInstant(
                                      java.time.Instant.ofEpochMilli(metadata.timestamp()),
                                      java.time.ZoneId.systemDefault()
                                  ).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        logger.info("   🔑 Key: {}", key);
                        logger.info("   💬 Message: {}", message);
                        logger.info("   --------------------------------");
                    } else {
                        // Error - mostrar detalles del fallo
                        logger.error("❌ Error enviando mensaje:", exception);
                        logger.error("   🔑 Key: {}", key);
                        logger.error("   💬 Message: {}", message);
                        logger.error("   --------------------------------");
                    }
                });

                producer.flush();
                
                logger.debug("Mensaje encolado para envío");
            }
            
            // Asegurar que todos los mensajes se envíen antes de cerrar
            logger.info("Enviando mensajes pendientes...");
            producer.flush();
            logger.info("Producer cerrado correctamente");
            
        } catch (Exception e) {
            logger.error("Error fatal en el producer", e);
            System.exit(1);
        }
        
        logger.info("¡Hasta luego! 👋");
    }
}
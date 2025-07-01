package edu.devra.kafka.demo;

import edu.devra.kafka.config.KafkaConfig;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * Demostración de cómo funciona el particionado en Kafka
 * Crea un topic con múltiples particiones y envía mensajes con diferentes keys
 * para mostrar cómo se distribuyen entre las particiones
 */
public class MultiPartitionDemo {
    private static final Logger logger = LoggerFactory.getLogger(MultiPartitionDemo.class);
    
    // Configuraciones del demo
    private static final int PARTITION_COUNT = 3;
    private static final short REPLICATION_FACTOR = 1;
    
    public static void main(String[] args) {
        logger.info("=== Demo de Múltiples Particiones ===");
        logger.info("Topic: {}", KafkaConfig.MULTI_PARTITION_TOPIC);
        logger.info("Particiones: {}", PARTITION_COUNT);
        logger.info("Factor de Replicación: {}", REPLICATION_FACTOR);
        logger.info("=====================================");
        
        try {
            // Paso 1: Crear el topic con múltiples particiones
            createMultiPartitionTopic();
            
            // Paso 2: Enviar mensajes con diferentes estrategias de particionado
            demonstratePartitioning();
            
            logger.info("🎉 Demo completado exitosamente!");
            logger.info("💡 Revisa Kafka UI en http://localhost:8080 para ver la distribución");
            
        } catch (Exception e) {
            logger.error("❌ Error en el demo", e);
            System.exit(1);
        }
    }
    
    /**
     * Crea un topic con múltiples particiones usando Admin Client
     */
    private static void createMultiPartitionTopic() throws ExecutionException, InterruptedException {
        logger.info("🔧 Creando topic con múltiples particiones...");
        
        try (var admin = AdminClient.create(KafkaConfig.getAdminProps())) {
            
            // Verificar si el topic ya existe
            var existingTopics = admin.listTopics().names().get();
            if (existingTopics.contains(KafkaConfig.MULTI_PARTITION_TOPIC)) {
                logger.info("ℹ️ El topic '{}' ya existe, continuando...", KafkaConfig.MULTI_PARTITION_TOPIC);
                return;
            }
            
            // Crear nuevo topic
            var newTopic = new NewTopic(
                KafkaConfig.MULTI_PARTITION_TOPIC, 
                PARTITION_COUNT, 
                REPLICATION_FACTOR
            );
            
            admin.createTopics(Collections.singletonList(newTopic))
                 .all()
                 .get(); // Bloquear hasta que se complete
            
            logger.info("✅ Topic '{}' creado con {} particiones", 
                       KafkaConfig.MULTI_PARTITION_TOPIC, PARTITION_COUNT);
        }
    }
    
    /**
     * Demuestra diferentes estrategias de particionado
     */
    private static void demonstratePartitioning() {
        logger.info("📨 Enviando mensajes para demostrar particionado...");
        
        try (var producer = new KafkaProducer<String, String>(KafkaConfig.getProducerProps())) {
            
            // Demo 1: Particionado por key (hash)
            demonstrateKeyBasedPartitioning(producer);
            
            // Demo 2: Particionado round-robin (sin key)
            demonstrateRoundRobinPartitioning(producer);
            
            // Demo 3: Particionado manual
            demonstrateManualPartitioning(producer);
            
            // Asegurar que todos los mensajes se envíen
            producer.flush();
            logger.info("✅ Todos los mensajes enviados");
        }
    }
    
    /**
     * Demuestra particionado basado en key (hash de la key)
     */
    private static void demonstrateKeyBasedPartitioning(KafkaProducer<String, String> producer) {
        logger.info("🔑 Demo 1: Particionado por Key (Hash)");
        
        // Usar keys que representen usuarios
        String[] userKeys = {"user-alice", "user-bob", "user-charlie", "user-alice", "user-bob"};
        
        for (int i = 0; i < userKeys.length; i++) {
            String key = userKeys[i];
            String value = String.format("Mensaje %d del %s", i + 1, key);
            
            var record = new ProducerRecord<>(KafkaConfig.MULTI_PARTITION_TOPIC, key, value);
            
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    logger.info("📤 [KEY-HASH] Key: '{}' → Partición: {} | Offset: {} | Valor: '{}'", 
                               key, metadata.partition(), metadata.offset(), value);
                } else {
                    logger.error("❌ Error enviando mensaje con key: {}", key, exception);
                }
            });
            
            // Pequeña pausa para mejor visualización
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        logger.info("ℹ️ Observa que mensajes con la misma key van a la misma partición");
    }
    
    /**
     * Demuestra particionado round-robin (sin key)
     */
    private static void demonstrateRoundRobinPartitioning(KafkaProducer<String, String> producer) {
        logger.info("🔄 Demo 2: Particionado Round-Robin (Sin Key)");
        
        for (int i = 1; i <= 6; i++) {
            String value = String.format("Mensaje Round-Robin #%d", i);
            
            // Sin key, Kafka usará round-robin
            var record = new ProducerRecord<String, String>(KafkaConfig.MULTI_PARTITION_TOPIC, null, value);
            
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    logger.info("📤 [ROUND-ROBIN] Sin Key → Partición: {} | Offset: {} | Valor: '{}'", 
                               metadata.partition(), metadata.offset(), value);
                } else {
                    logger.error("❌ Error enviando mensaje round-robin", exception);
                }
            });
            
            // Pequeña pausa para mejor visualización
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        logger.info("ℹ️ Observa que los mensajes se distribuyen secuencialmente entre particiones");
    }
    
    /**
     * Demuestra particionado manual (especificando partición)
     */
    private static void demonstrateManualPartitioning(KafkaProducer<String, String> producer) {
        logger.info("🎯 Demo 3: Particionado Manual (Partición Específica)");
        
        for (int partition = 0; partition < PARTITION_COUNT; partition++) {
            String value = String.format("Mensaje específico para partición %d", partition);
            
            // Especificar partición manualmente
            var record = new ProducerRecord<>(
                KafkaConfig.MULTI_PARTITION_TOPIC, 
                partition,  // Partición específica
                "manual-key-" + partition, 
                value
            );

            int finalPartition = partition;
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    logger.info("📤 [MANUAL] Partición especificada: {} → Partición real: {} | Offset: {} | Valor: '{}'",
                            finalPartition, metadata.partition(), metadata.offset(), value);
                } else {
                    logger.error("❌ Error enviando mensaje a partición {}", finalPartition, exception);
                }
            });
            
            // Pequeña pausa para mejor visualización
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        logger.info("ℹ️ Observa que cada mensaje fue exactamente a la partición especificada");
    }
}
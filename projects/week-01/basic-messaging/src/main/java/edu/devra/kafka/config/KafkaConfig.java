package edu.devra.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * Configuración centralizada para Kafka
 * Contiene todas las propiedades necesarias para Producers y Consumers
 */
public class KafkaConfig {
    
    // Configuración del servidor
    public static final String BOOTSTRAP_SERVERS = "localhost:9092";
    
    // Topics
    public static final String TOPIC_NAME = "my-first-topic";
    public static final String MULTI_PARTITION_TOPIC = "multi-partition-topic";
    
    /**
     * Configuración para Producer
     * @return Properties configuradas para el producer
     */
    public static Properties getProducerProps() {
        var props = new Properties();
        
        // Configuración básica
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        // Configuraciones para reliability y aprendizaje
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // Esperar confirmación de todas las réplicas
        props.put(ProducerConfig.RETRIES_CONFIG, 3); // Reintentos en caso de error
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // Evitar duplicados
        
        // Configuraciones de performance (conservadoras para aprendizaje)
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // 16KB batch size
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5); // Esperar 5ms para crear batch
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // 32MB buffer
        
        return props;
    }
    
    /**
     * Configuración para Consumer
     * @param groupId ID del grupo de consumidores
     * @return Properties configuradas para el consumer
     */
    public static Properties getConsumerProps(String groupId) {
        var props = new Properties();
        
        // Configuración básica
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        
        // Configuraciones importantes para aprendizaje
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Leer desde el principio si no hay offset
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // Manual commit para control completo
        
        // Configuraciones de performance
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100); // Máximo 100 records por poll
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024); // Mínimo 1KB por fetch
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500); // Esperar máximo 500ms
        
        return props;
    }
    
    /**
     * Configuración específica para Admin Client
     * @return Properties para operaciones administrativas
     */
    public static Properties getAdminProps() {
        var props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000); // 30 segundos timeout
        return props;
    }
}
package edu.devra.kafka.chat;

import edu.devra.kafka.chat.model.ChatMessage;
import edu.devra.kafka.config.KafkaConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatProducer {
    private static final Logger logger = LoggerFactory.getLogger(ChatProducer.class);
    
    private final KafkaProducer<String, String> producer;
    private final String userName;
    private final String room;
    private final String topicName;
    
    public ChatProducer(String userName, String room, String topicName) {
        this.producer = new KafkaProducer<>(KafkaConfig.getProducerProps());
        this.userName = userName;
        this.room = room;
        this.topicName = topicName;
    }
    
    public void sendMessage(String message) {
        var chatMessage = ChatMessage.create(userName, message, room);
        var record = new ProducerRecord<>(topicName, room, chatMessage.toJson());
        
        producer.send(record, (metadata, exception) -> {
            if (exception == null) {
                logger.debug("Mensaje enviado: partition={}, offset={}", 
                           metadata.partition(), metadata.offset());
            } else {
                logger.error("Error enviando mensaje", exception);
            }
        });
    }
    
    public void close() {
        producer.close();
    }
}
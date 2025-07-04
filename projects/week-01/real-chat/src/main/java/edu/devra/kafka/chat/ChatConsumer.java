package edu.devra.kafka.chat;

import edu.devra.kafka.chat.model.ChatMessage;
import edu.devra.kafka.config.KafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

public class ChatConsumer {
    private static final Logger logger = LoggerFactory.getLogger(ChatConsumer.class);
    
    private final KafkaConsumer<String, String> consumer;
    private final Consumer<ChatMessage> messageHandler;
    private volatile boolean running = true;
    
    public ChatConsumer(String groupId, String topicName, Consumer<ChatMessage> messageHandler) {
        this.consumer = new KafkaConsumer<>(KafkaConfig.getConsumerProps(groupId));
        this.messageHandler = messageHandler;
        this.consumer.subscribe(List.of(topicName));
    }
    
    public void startConsuming() {
        try {
            while (running) {
                var records = consumer.poll(Duration.ofMillis(100));
                
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        var chatMessage = ChatMessage.fromJson(record.value());
                        messageHandler.accept(chatMessage);
                        consumer.commitSync();
                    } catch (Exception e) {
                        logger.error("Error procesando mensaje", e);
                    }
                }
            }
        } catch (WakeupException e) {
            logger.info("Consumer interrumpido");
        } finally {
            consumer.close();
        }
    }
    
    public void stop() {
        running = false;
        consumer.wakeup();
    }
}
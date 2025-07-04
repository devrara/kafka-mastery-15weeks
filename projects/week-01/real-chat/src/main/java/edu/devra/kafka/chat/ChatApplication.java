package edu.devra.kafka.chat;

import edu.devra.kafka.chat.model.ChatMessage;
import edu.devra.kafka.config.KafkaConfig;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatApplication {
    private static final Logger logger = LoggerFactory.getLogger(ChatApplication.class);
    
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java ChatApplication <username> <room>");
            System.exit(1);
        }
        
        String userName = args[0];
        String room = args[1];
        String groupId = "chat-group-" + room + "-" + userName;
        String topicName = "chat-room-" + room;

         try (var admin = AdminClient.create(KafkaConfig.getAdminProps())) {
            var topic = new NewTopic(topicName, 1, (short) 1); // 1 partición, 1 réplica
            admin.createTopics(List.of(topic)).all().get();
            logger.info("Topic '{}' creado exitosamente", topicName);
        } catch (TopicExistsException e) {
            logger.info("El topic '{}' ya existe", topicName);
        } catch (Exception e) {
            logger.error("Error al crear el topic '{}'", topicName, e);
        }
        
        logger.info("=== Chat iniciado ===");
        logger.info("Usuario: {}", userName);
        logger.info("Sala: {}", room);
        logger.info("Escribe mensajes (escribe '/exit' para salir):");
        
        // Crear producer
        var producer = new ChatProducer(userName, room, topicName);
        
        // Crear consumer en hilo separado
        var consumer = new ChatConsumer(groupId, topicName, message -> displayMessage(message, userName));
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(consumer::startConsuming);
        
        // Configurar shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Cerrando chat...");
            consumer.stop();
            producer.close();
            executor.shutdown();
        }));
        
        // Loop principal para entrada de usuario
        try (var scanner = new Scanner(System.in)) {
            while (true) {
                String input = scanner.nextLine();
                
                if ("/exit".equals(input)) {
                    break;
                }
                
                if (!input.trim().isEmpty()) {
                    producer.sendMessage(input);
                }
            }
        }
        
        // Cleanup
        consumer.stop();
        producer.close();
        executor.shutdown();
        
        logger.info("Chat cerrado");
    }
    
    private static void displayMessage(ChatMessage message, String currentUser) {
        // No mostrar nuestros propios mensajes
        if (!message.user().equals(currentUser)) {
            System.out.printf("[%s] %s: %s%n", 
                            message.timestamp().substring(11, 19), // Solo la hora
                            message.user(), 
                            message.message());
        }
    }
}
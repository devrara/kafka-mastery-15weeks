package edu.devra.kafka.chat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record ChatMessage(
    @JsonProperty("user") String user,
    @JsonProperty("message") String message,
    @JsonProperty("timestamp") String timestamp,
    @JsonProperty("room") String room
) {
    private static final ObjectMapper mapper = new ObjectMapper();
    
    public static ChatMessage create(String user, String message, String room) {
        return new ChatMessage(
            user, 
            message, 
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            room
        );
    }
    
    public String toJson() {
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializando mensaje", e);
        }
    }
    
    public static ChatMessage fromJson(String json) {
        try {
            return mapper.readValue(json, ChatMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializando mensaje", e);
        }
    }
}
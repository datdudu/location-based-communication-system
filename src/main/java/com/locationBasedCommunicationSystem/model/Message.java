package com.locationBasedCommunicationSystem.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Data
public class Message {
    private String from;
    private String to;
    private String content;
    private LocalDateTime timestamp;
    private boolean sync;

    public Message(String from, String to, String content, boolean sync) {
        this.from = from;
        this.to = to;
        this.content = content;
        this.sync = sync;
        this.timestamp = LocalDateTime.now();
    }

    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isSync() { return sync; }
}
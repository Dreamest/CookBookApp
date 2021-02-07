package com.dreamest.cookbookapp.logic;

public class ChatMessage {
    private String text;
    private String senderID;
    private String senderName;
    private long timestamp;

    public ChatMessage() {
    }

    public String getSenderName() {
        return senderName;
    }

    public ChatMessage setSenderName(String senderName) {
        this.senderName = senderName;
        return this;
    }

    public String getSenderID() {
        return senderID;
    }

    public ChatMessage setSenderID(String senderID) {
        this.senderID = senderID;
        return this;
    }

    public String getText() {
        return text;
    }

    public ChatMessage setText(String text) {
        this.text = text;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ChatMessage setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}

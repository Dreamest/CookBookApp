package com.dreamest.cookbookapp.logic;

public class ChatMessage {
    private boolean isSentByCurrent;
    private String text;
    private long timestamp;
    private boolean isSeen;

    public ChatMessage() {
    }

    public boolean isSentByCurrent() {
        return isSentByCurrent;
    }

    public ChatMessage setSentByCurrent(boolean sentByCurrent) {
        isSentByCurrent = sentByCurrent;
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

    public boolean isSeen() {
        return isSeen;
    }

    public ChatMessage setSeen(boolean seen) {
        isSeen = seen;
        return this;
    }
}

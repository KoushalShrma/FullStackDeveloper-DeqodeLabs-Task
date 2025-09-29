package com.deqode.financeresearch.service;

@FunctionalInterface
public interface StreamCallback {
    void sendChunk(String connectionId, String eventType, String data);
}
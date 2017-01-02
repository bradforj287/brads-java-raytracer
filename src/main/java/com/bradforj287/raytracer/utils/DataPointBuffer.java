package com.bradforj287.raytracer.utils;

import java.util.ArrayDeque;

public class DataPointBuffer {
    private final ArrayDeque<Integer> buffer;
    private final int maxSize;

    public DataPointBuffer(final int maxSize) {
        this.maxSize = maxSize;
        this.buffer = new ArrayDeque<>();
    }

    public synchronized void addToBuffer(int data) {
        if (buffer.size() >= maxSize) {
            buffer.removeFirst();
        }
        buffer.add(data); // push to end of buffer
    }

    public synchronized int getAvg() {
        if (buffer.isEmpty()) {
            return 0;
        }
        int size = buffer.size();
        long sum = 0;
        for (Integer data : buffer) {
            sum += data;
        }
        long avg = sum/size;
        return (int) avg;
    }
}

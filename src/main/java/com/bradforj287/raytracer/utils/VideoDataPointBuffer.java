package com.bradforj287.raytracer.utils;

import java.util.LinkedList;

/**
 * Class that represents a fixed-size buffer for storing video data points.
 *
 * @author bjones
 */
public class VideoDataPointBuffer {
    private static final int BUFFER_MAX_SIZE = 100;
    private static final long RUNNING_AVERAGE_DURATION = 1000;

    private LinkedList<Long> buffer = new LinkedList<Long>();

    public synchronized void addToBuffer(Long data) {
        if (buffer.size() >= BUFFER_MAX_SIZE) {
            buffer.removeFirst();
        }
        buffer.add(data); // push to end of buffer
        clearOldPoints();
    }

    private void clearOldPoints() {
        long currentTime = System.currentTimeMillis();
        while (buffer.size() != 0) {
            Long point = buffer.peek();
            if (currentTime - point > RUNNING_AVERAGE_DURATION) {
                buffer.removeFirst();

            } else {
                return;
            }
        }
    }

    public synchronized double getFramesPerSecond() {
        clearOldPoints();

        if (buffer.size() == 0) {
            return 0;
        }

        long timeDelta = System.currentTimeMillis() - buffer.getFirst();

        if (timeDelta == 0) {
            return 0;
        }

        double realDelta = (double) timeDelta;
        realDelta = realDelta / 1000;// convert to seconds

        double frameRate = (double) buffer.size() / realDelta;
        return frameRate;
    }

}
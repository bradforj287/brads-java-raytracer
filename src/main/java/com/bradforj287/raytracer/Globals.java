package com.bradforj287.raytracer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

public class Globals {
    public static int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    public static ExecutorService executorService;
    public static EventBus rayTraceEventBus;

    static {
        executorService = Executors.newFixedThreadPool(NUM_THREADS);
        rayTraceEventBus =  new AsyncEventBus(executorService);
    }
}

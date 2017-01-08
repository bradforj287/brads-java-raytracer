package com.bradforj287.raytracer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Globals {
    public static int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    public static ExecutorService executorService;

    static {
        executorService = Executors.newFixedThreadPool(NUM_THREADS);
    }
}

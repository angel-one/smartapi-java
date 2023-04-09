package com.angelbroking.smartapi.ticker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceSingleton {
    private static final ExecutorService instance = Executors.newFixedThreadPool(10);

    private ExecutorServiceSingleton() {
        // private constructor to prevent instantiation
    }

    public static ExecutorService getInstance() {
        return instance;
    }
}

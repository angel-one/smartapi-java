package com.angelbroking.smartapi.sample.ticker;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class SmartApiTickerScheduler {

    private final ScheduledExecutorService scheduledExecutorService;

    public SmartApiTickerScheduler() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void schedule(Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        scheduledExecutorService.scheduleAtFixedRate(runnable, delay, period, timeUnit);
    }

    public void shutdown() {
        scheduledExecutorService.shutdown();
    }
}

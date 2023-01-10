package me.windyteam.kura.utils.other;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreading {
    private static final ExecutorService SERVICE = Executors.newScheduledThreadPool(Math.max(Math.min(Runtime.getRuntime().availableProcessors() / 4, 4), 1));

    public static void runAsync(Runnable task) {
        SERVICE.execute(task);
    }
}
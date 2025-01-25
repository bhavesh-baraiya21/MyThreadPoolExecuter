package com.example.threadPool;

public class ThreadUtils {
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // You can log the exception or re-interrupt the thread if needed
            Thread.currentThread().interrupt();
            System.err.println("Thread was interrupted during sleep: " + e.getMessage());
        }
    }
}

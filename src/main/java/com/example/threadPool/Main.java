package com.example.threadPool;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Thread.currentThread().setName("main");

        // Configuration
        String urlsFilePath = "/Users/bhavesh_baraiya21/MyThreadPoolExecuter/src/main/java/com/example/threadPool/urls.txt";
        String outputDirectory = "/Users/bhavesh_baraiya21/MyThreadPoolExecuter/downloads";

        // Create output directory if it doesn't exist
        File outputDir = new File(outputDirectory);
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }

        // Read URLs from file
        List<String> urls = readUrlsFromFile(urlsFilePath);
        if (urls.isEmpty()) {
            System.err.println("No URLs found in the file.");
            return;
        }

        // Initialize thread pool
        MyBlockingQueue taskQueue = new MyBlockingQueue(10);
        MyThreadPoolExecuter threadPool = new MyThreadPoolExecuter(2, 7, 100, taskQueue);

        PrintingThreadLogs threadLogs = new PrintingThreadLogs(threadPool);
        threadLogs.start();

        // Submit tasks to thread pool
        for (String url : urls) {
            String fileName = getFileNameFromUrl(url);
            String outputPath = outputDirectory + File.separator + fileName;

            FileDownloadTask task = new FileDownloadTask(url, outputPath);
            threadPool.execute(task);
        }

        // Wait for downloads to complete
        while (taskQueue.currSize > 0) {
            ThreadUtils.sleep(1000);
        }

        threadPool.shutdown();
        System.out.println("All downloads completed.");
        threadLogs.interrupt();
    }

    private static List<String> readUrlsFromFile(String filePath) {
        List<String> urls = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                urls.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading URLs from file: " + e.getMessage());
        }
        return urls;
    }

    private static String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}


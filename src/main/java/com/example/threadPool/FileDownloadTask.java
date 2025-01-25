package com.example.threadPool;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloadTask implements Runnable {
    private final String url;
    private final String outputFilePath;
    static int taskId = 100;
    int currTaskId;

    public FileDownloadTask(String url, String outputFilePath) {
        this.url = url;
        this.outputFilePath = outputFilePath;
        currTaskId = taskId;
        taskId++;
    }

    @Override
    public void run() {
        try {
            downloadFile(url, outputFilePath);
        } catch (Exception e) {
            System.err.println("Error downloading file from " + url + ": " + e.getMessage());
        }
    }

    private void downloadFile(String fileUrl, String outputPath) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + responseCode);
        }

        int contentLength = connection.getContentLength();
        InputStream inputStream = connection.getInputStream();
        FileOutputStream outputStream = new FileOutputStream(outputPath);

        byte[] buffer = new byte[4096];
        int bytesRead;
        int totalBytesRead = 0;

        System.out.println("Downloading: " + fileUrl);
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            totalBytesRead += bytesRead;

            int progress = (int) (((double) totalBytesRead / contentLength) * 100);
            printProgressBar(currTaskId,"Task" + String.valueOf(currTaskId), progress);
        }

        outputStream.close();
        inputStream.close();

        System.out.println("\nDownload completed: " + fileUrl);
    }

    private static synchronized void printProgressBar(int line, String taskName, int progress) {
        // Calculate the filled portion of the progress bar
        int filledLength = (progress * 50) / 100;
        String progressBar = "=".repeat(filledLength) + " ".repeat(50 - filledLength);

        // Move the cursor to the specific line and update the progress bar
        System.out.printf("\033[%d;0H", line); // Move to the specific line (line number corresponds to thread ID)
        System.out.print("\033[K"); // Clear the line
        System.out.printf("%s [%s] %d%%%n", taskName, progressBar, progress);
    }
}

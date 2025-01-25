package com.example.threadPool;

public class SomeTask implements Runnable {
    static int taskId = 10;
    public void run(){
        int currId = taskId;
        taskId++;

        System.out.print("\033[2J"); // Clear the screen
        System.out.flush();

        for(int i=0; i<10; i++){
            printProgressBar(currId, "Task-" + String.valueOf(currId), i*10);
            ThreadUtils.sleep(1000);
        }

        System.out.println(Thread.currentThread().getName() + " have completed this task.");
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

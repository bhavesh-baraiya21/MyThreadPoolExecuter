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
        int filledLength = (progress * 50) / 100;
        String progressBar = "=".repeat(filledLength) + " ".repeat(50 - filledLength);

        System.out.printf("\033[%d;0H", line);
        System.out.print("\033[K");
        System.out.printf("%s [%s] %d%%%n", taskName, progressBar, progress);
    }
}

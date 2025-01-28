package com.example.threadPool;

public class WorkerThread extends Thread {
    private final MyBlockingQueue taskQueue;
    boolean isThreadWorking = false;
    long lastWorkedTime = System.currentTimeMillis();
    MyThreadPoolExecuter ex;

    public WorkerThread(MyBlockingQueue taskQueue, MyThreadPoolExecuter ex) {
        this.taskQueue = taskQueue;
        this.ex = ex;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                Runnable task = taskQueue.getTask();
                if (task != null) {
                    isThreadWorking = true;
                    task.run();
                    isThreadWorking = false;
                    lastWorkedTime = System.currentTimeMillis();
                    synchronized (ex.isWorking) {
                        ex.isWorking.notify();
                    }
                }
            } catch (Exception exp) {
                isThreadWorking = false;
                lastWorkedTime = System.currentTimeMillis();
                synchronized (ex.isWorking) {
                    ex.isWorking.notify();
                }
                Thread.currentThread().interrupt();
                exp.printStackTrace();
                break;
            }

            if (System.currentTimeMillis() - lastWorkedTime > ex.keepAliveTime) {
                synchronized (ex.isWorking) {
                    ex.isWorking.notify();
                }
            }
        }
    }
}

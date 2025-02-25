package com.example.threadPool;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyThreadPoolExecuter {
    Integer minThread;
    Integer maxThread;
    Integer keepAliveTime;
    MyBlockingQueue workQueue;
    Integer threadIdAssigner = 1000;
    boolean isShutdown = false;
    final Thread isWorking = new IsWorking(this);
    final Thread addThread = new AddThread(this);

    final List<WorkerThread> threadList = new CopyOnWriteArrayList<>();

    MyThreadPoolExecuter(Integer minT, Integer maxT, Integer keepAliveTime, MyBlockingQueue queue) {
        this.minThread = minT;
        this.maxThread = maxT;
        this.keepAliveTime = keepAliveTime;
        this.workQueue = queue;

        for (int i=0; i<minT; i++) {
            WorkerThread t = new WorkerThread(workQueue, this);
            t.setName("worker_" + threadIdAssigner);
            threadIdAssigner++;
            threadList.add(t);
            t.start();
            synchronized (isWorking) {
                isWorking.notify();
            }
        }

        isWorking.setName("IsWorking Checker");
        isWorking.start();

        addThread.setName("Extra Thread Deleter");
        addThread.start();
    }

    void execute(Runnable task) {
        if (workQueue.addTask(task) == -1) {
            System.out.println("Queue is full.");
        } else {
            synchronized (addThread) {
                addThread.notify();
            }
        }
    }

    void shutdown() {
        isShutdown = true;
        isWorking.interrupt();
        addThread.interrupt();

        while (!threadList.isEmpty()) {
            for (WorkerThread t: threadList) {
                if (!t.isThreadWorking) {
                    t.interrupt();
                    threadList.remove(t);
                }
            }
        }
    }
}

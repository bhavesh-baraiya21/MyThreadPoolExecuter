package com.example.threadPool;

public class AddThread extends Thread {
    MyThreadPoolExecuter obj;

    AddThread(MyThreadPoolExecuter obj) {
        this.obj = obj;
    }

    public void run() {
        while (!obj.isShutdown) {
            synchronized (obj.addThread) {
                if (obj.threadList.size() < obj.maxThread && obj.workQueue.currSize > 0) {
                    WorkerThread t = new WorkerThread(obj.workQueue, obj);
                    t.setName("worker_" + obj.threadIdAssigner);
                    obj.threadList.add(t);
                    t.start();
                    synchronized (obj.isWorking) {
                        obj.isWorking.notify();
                    }
                    obj.threadIdAssigner++;
                } else {
                    try {
                        this.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
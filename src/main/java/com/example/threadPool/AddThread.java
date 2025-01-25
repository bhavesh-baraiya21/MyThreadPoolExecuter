package com.example.threadPool;

public class AddThread extends Thread{
    MyThreadPoolExecuter obj;

    AddThread(MyThreadPoolExecuter obj){
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

                    // Notify the IsWorking thread about the change
                    synchronized (obj.isWorking) {
                        obj.isWorking.notify();
                    }

                    System.out.println("A new thread " + t.getName() + " is added by AddThread class");
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
        System.out.println(Thread.currentThread().getName() + " has exited.");
    }

}

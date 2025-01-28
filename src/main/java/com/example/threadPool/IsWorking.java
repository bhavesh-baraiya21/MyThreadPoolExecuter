package com.example.threadPool;

public class IsWorking extends Thread {

    MyThreadPoolExecuter obj;

    IsWorking(MyThreadPoolExecuter obj) {
        this.obj = obj;
    }

    @Override
    public void run() {
        while (!obj.isShutdown) {
            boolean waitFlag = false;
            for (WorkerThread t: obj.threadList) {
                if (!t.isThreadWorking && obj.threadList.size() > obj.minThread && obj.keepAliveTime < (System.currentTimeMillis() - t.lastWorkedTime)) {
                    obj.threadList.remove(t);
                    t.interrupt();
                    synchronized (obj.addThread) {
                        obj.addThread.notify();
                    }
                    waitFlag = true;
                }
            }

            if (waitFlag) {
                synchronized (this) {
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

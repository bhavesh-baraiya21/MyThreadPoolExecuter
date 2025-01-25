package com.example.threadPool;

public class PrintingThreadLogs extends Thread {
    MyThreadPoolExecuter ex;

    PrintingThreadLogs(MyThreadPoolExecuter ex){
        this.ex = ex;
    }

    public void run(){
        while(true){
            System.out.println("Currently " + ex.threadList.size() + " threads are is running. and " + ex.workQueue.currSize + " tasks remaining.");

            ThreadUtils.sleep(1000);

            if(Thread.currentThread().isInterrupted()){
                System.out.println("Logs are signing off...");
                break;
            }
        }
    }
}
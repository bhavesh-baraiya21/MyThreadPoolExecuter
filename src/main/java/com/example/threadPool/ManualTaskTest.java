package com.example.threadPool;

public class ManualTaskTest {
    public static void main(String[] args) throws InterruptedException {
        long startTimeThread = System.currentTimeMillis();

        Thread.currentThread().setName("main");

        //create a blocking queue.
        MyBlockingQueue queue = new MyBlockingQueue(20);

        MyThreadPoolExecuter ex = new MyThreadPoolExecuter(2, 7, 2000, queue);

        PrintingThreadLogs printLogs = new PrintingThreadLogs(ex);
        printLogs.start();

        for(int i=0; i<20; i++){
            SomeTask task = new SomeTask();
            ex.execute(task);
        }

        while(queue.currSize > 0){
            ThreadUtils.sleep(100);

            System.out.println("Threads in working queue : " + queue.currSize);
        }

        System.out.println("Threads in working queue : " + queue.currSize);

        ex.shutdown();
        System.out.println("ThreadPoolExecuter exited...");

        System.out.println("Currently " + Thread.activeCount() + " thread are still remains.");

        System.out.println("ThreadPoolExecuter exited.");
        printLogs.interrupt();

        long endTimeThread = System.currentTimeMillis();

        System.out.println("Total time taken is: " + (endTimeThread-startTimeThread));

        System.out.println("Now same tasks ran sequentially.");
        long startTimeSeq = System.currentTimeMillis();

        for(int i=0; i<20; i++){
            SomeTask task = new SomeTask();
            task.run();
        }

        long endTimeSeq = System.currentTimeMillis();

        System.out.println("Comparison: ThreadTime - " + (endTimeThread-startTimeThread) + " , SeqTime- " + (endTimeSeq - startTimeSeq));
    }
}

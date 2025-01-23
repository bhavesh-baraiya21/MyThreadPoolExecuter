import java.util.concurrent.*;
import java.util.*;

class WorkerThread extends Thread {
    private final MyBlockingQueue taskQueue;
    boolean isThreadWorking = false;

    public WorkerThread(MyBlockingQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Runnable task = taskQueue.getTask();
                if(task != null){
                    isThreadWorking = true;
                    task.run();
                    isThreadWorking = false;
                }
            } catch (Exception ex) {
                System.out.println(Thread.currentThread().getName() + " is interrupted....");
//                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

class MyThreadPoolExecuter {
    Integer minThread;
    Integer maxThread;
    Integer keepAliveTime;
    MyBlockingQueue workQueue;
    Integer threadIdAssigner = 1000;
    boolean isShutdown = false;

    List<WorkerThread> threadList = new ArrayList<WorkerThread>();

    MyThreadPoolExecuter(Integer minT, Integer maxT, Integer keepAliveTime, MyBlockingQueue queue){
        this.minThread = minT;
        this.maxThread = maxT;
        this.keepAliveTime = keepAliveTime;
        this.workQueue = queue;

        //creating minT number of threads
        for(int i=0; i<minT; i++){
            WorkerThread t = new WorkerThread(workQueue);
            t.setName("worker_" + String.valueOf(threadIdAssigner));
            threadIdAssigner++;
            threadList.add(t);
            t.start();
        }

        //create a new thread for checking thread is working.
        Thread t1 = new IsWorking(this);
        t1.setName("IsWorking Checker");
        t1.start();

        //create a new thread for deleting extra threads.
        Thread t2 = new AddThread(this);
        t2.setName("Extra Thread Deleter");
        t2.start();
    }

    void execute(Runnable task){
        if(workQueue.addTask(task) == -1){
            System.out.println("Queue is full.");
        }
    }

    void shutdown(){
        isShutdown = true;

        Thread[] workingThreads = new Thread[Thread.activeCount()];
        int cnt = Thread.enumerate(workingThreads);
        System.out.println("Currently " + cnt + " threads are working.");

        for(WorkerThread t: threadList){
            t.interrupt();
            System.out.println(t.getName() + "is got interrupted by shutdown().");
        }
    }
}

class AddThread extends Thread{
    MyThreadPoolExecuter obj;

    AddThread(MyThreadPoolExecuter obj){
        this.obj = obj;
    }

    public void run(){
        while(!obj.isShutdown){
            if(Thread.activeCount() <= obj.maxThread && obj.workQueue.currSize > 0){
                WorkerThread t = new WorkerThread(obj.workQueue);
                t.setName("worker_" + String.valueOf(obj.threadIdAssigner));
                obj.threadList.add(t);
                obj.threadIdAssigner++;
                t.start();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + " have exited.");
    }
}

class IsWorking extends Thread {

    MyThreadPoolExecuter obj;

    IsWorking(MyThreadPoolExecuter obj){
        this.obj = obj;
    }

    @Override
    public void run(){
        while(!obj.isShutdown){

            for(WorkerThread t: obj.threadList){
                if(!t.isThreadWorking && Thread.activeCount() > obj.minThread){
                    obj.threadList.remove(t);

                    t.interrupt();
                    System.out.println(t.getName() + " is interrupted by IsWorking.");
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + " have exited.");
    }
}

class MyBlockingQueue {
    Integer maxSize;
    Integer head;
    Integer tail;
    Integer currSize;
    ArrayList<Runnable> queue = new ArrayList<>();

    MyBlockingQueue(Integer sz){
        this.maxSize = sz;
        this.head = -1;
        this.tail = 0;
        this.currSize = 0;

        for(int i=0; i<sz; i++){
            queue.add(null);
        }
    }

    synchronized int addTask(Runnable task){
        if(currSize.equals(maxSize)){
            return -1;
        }

        head = (head+1)%maxSize;
        queue.set(head, task);
        currSize++;

        return 0;
    }

    synchronized Runnable getTask(){
        if(currSize == 0){
            return null;
        }

        int ind = tail;
        tail = (tail+1)%maxSize;
        currSize--;

        return queue.get(ind);
    }
}

class SomeTask implements Runnable {
    static int taskId = 10;
    public void run(){
        int currId = taskId;
        taskId++;

        System.out.print("\033[2J"); // Clear the screen
        System.out.flush();

        for(int i=0; i<10; i++){
            printProgressBar(currId, "Task-" + String.valueOf(currId), i*10);
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex){
                ex.printStackTrace();
            }
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

public class MyThreadPoolExecuterTest {
    public static void main(String[] args){
        Thread.currentThread().setName("main");

        //create a blocking queue.
        MyBlockingQueue queue = new MyBlockingQueue(20);

        MyThreadPoolExecuter ex = new MyThreadPoolExecuter(5, 10, 100, queue);

        for(int i=0; i<10; i++){
            SomeTask task = new SomeTask();
            ex.execute(task);
        }

        try {
            Thread.sleep(5000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        for(int i=0; i<10; i++){
            SomeTask task = new SomeTask();
            ex.execute(task);

            try {
                Thread.sleep(1000);
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(5000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        while(queue.currSize > 0){
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e){
                e.printStackTrace();
            }
            System.out.println("Threads in working queue : " + queue.currSize);
        }

        System.out.println("Threads in working queue : " + queue.currSize);


        ex.shutdown();
        System.out.println("ThreadPoolExecuter exited...");

        System.out.println("Currently " + Thread.activeCount() + " thread are still remains.");

        while(true){
            System.out.println(Thread.activeCount());

            try {
                Thread.sleep(1000);
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}

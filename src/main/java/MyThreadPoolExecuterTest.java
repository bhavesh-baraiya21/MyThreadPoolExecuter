import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

class WorkerThread extends Thread {
    private final MyBlockingQueue taskQueue;

    public WorkerThread(MyBlockingQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Runnable task = taskQueue.getTaks();
                if(task == null){

                }
                else task.run();
            } catch (Exception ex) {
                Thread.currentThread().interrupt();
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

    MyThreadPoolExecuter(Integer minT, Integer maxT, Integer keepAliveTime, MyBlockingQueue queue){
        this.minThread = minT;
        this.maxThread = maxT;
        this.keepAliveTime = keepAliveTime;
        this.workQueue = queue;


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

    int addTask(Runnable task){
        if(currSize.equals(maxSize)){
            return -1;
        }

        head = (head+1)%maxSize;
        queue.set(head, task);

        return 0;
    }

    Runnable getTaks(){
        if(currSize == 0){
            return null;
        }

        int ind = tail;
        tail = (tail+1)%maxSize;

        return queue.get(ind);
    }
}


public class MyThreadPoolExecuterTest {
    public static void main(String[] args){

    }
}

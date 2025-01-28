package com.example.threadPool;

import java.util.ArrayList;

public class MyBlockingQueue {
    Integer maxSize;
    Integer head;
    Integer tail;
    Integer currSize;
    ArrayList<Runnable> queue = new ArrayList<>();

    MyBlockingQueue(Integer sz) {
        this.maxSize = sz;
        this.head = -1;
        this.tail = 0;
        this.currSize = 0;

        for (int i=0; i<sz; i++) {
            queue.add(null);
        }
    }

    synchronized int addTask(Runnable task) {
        if (currSize.equals(maxSize)) {
            return -1;
        }
        head = (head+1)%maxSize;
        queue.set(head, task);
        currSize++;

        return 0;
    }

    synchronized Runnable getTask() {
        if (currSize == 0) {
            return null;
        }
        int ind = tail;
        tail = (tail+1)%maxSize;
        currSize--;

        return queue.get(ind);
    }
}

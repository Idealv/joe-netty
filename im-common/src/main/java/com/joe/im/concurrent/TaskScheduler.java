package com.joe.im.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedQueue;


@Slf4j
public abstract class TaskScheduler<T> extends Thread{
    protected ConcurrentLinkedQueue<T> taskQueue = new ConcurrentLinkedQueue<>();

    protected long sleepTime = 200;

    @Override
    public void run() {
        while (true){
            handleTask();
            threadSleep(sleepTime);
        }
    }

    public void threadSleep(long sleepTime){
        try {
            sleep(sleepTime);
        }catch (InterruptedException e){
            log.error(e.getMessage());
        }
    }

    public void handleTask(){
        try {
            T t;
            if (taskQueue.peek()!=null){
                t = taskQueue.poll();
                handleTask(t);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    protected abstract void handleTask(T t);


}

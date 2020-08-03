package com.joe.im.concurrent;

import com.google.common.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class CallbackTaskScheduler extends Thread {
    private Queue<CallbackTask> executeTaskQueue = new ConcurrentLinkedDeque<>();
    private long sleepTime = 200;
    private ExecutorService jPool = Executors.newCachedThreadPool();
    private ListeningExecutorService gPool = MoreExecutors.listeningDecorator(jPool);

    private CallbackTaskScheduler() {
        //实例化后开启线程
        this.start();
    }

    private static CallbackTaskScheduler getInstance(){
        return Singleton.INSTANCE.getInstance();
    }

    private static enum Singleton{
        INSTANCE;

        CallbackTaskScheduler scheduler;

        Singleton(){
            scheduler = new CallbackTaskScheduler();
        }

        public CallbackTaskScheduler getInstance(){
            return scheduler;
        }
    }

    public static <R> void add(CallbackTask<R> callbackTask){
        getInstance().executeTaskQueue.add(callbackTask);
    }


    @Override
    public void run() {
        while (true){
            handleTask();
            //以200ms为间隔从队列中取任务
            threadSleep(sleepTime);
        }
    }

    private void threadSleep(long time){
        try {
            sleep(time);
        }catch (InterruptedException e){
            log.error(e.getMessage());
        }
    }

    private void handleTask(){
        try {
            CallbackTask callbackTask = null;
            if (executeTaskQueue.peek()!=null){
                callbackTask = executeTaskQueue.poll();
                handleTask(callbackTask);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    private <R> void handleTask(CallbackTask<R> callbackTask){
        ListenableFuture<R> future = gPool.submit(new Callable<R>() {
            @Override
            public R call() throws Exception {
                return callbackTask.execute();
            }
        });
        Futures.addCallback(future, new FutureCallback<R>() {
            @Override
            public void onSuccess(@Nullable R r) {
                callbackTask.onBack(r);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callbackTask.onException(throwable);
            }
        }, gPool);
    }
}

package com.joe.im.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FutureTaskScheduler extends TaskScheduler<ExecuteTask> {
    private ExecutorService pool = Executors.newFixedThreadPool(10);

    public static FutureTaskScheduler getInstance(){
        return Singleton.INSTANCE.getInstance();
    }

    private FutureTaskScheduler(){
        this.start();
    }

    public static void add(ExecuteTask task){
        getInstance().taskQueue.add(task);
    }

    @Override
    protected void handleTask(ExecuteTask executeTask) {
        pool.execute(new ExecuteRunnable(executeTask));
    }

    private enum Singleton{
        INSTANCE;

        FutureTaskScheduler scheduler;

        Singleton(){
            scheduler = new FutureTaskScheduler();
        }

        public FutureTaskScheduler getInstance(){
            return scheduler;
        }
    }

    class ExecuteRunnable implements Runnable{
        private ExecuteTask executeTask;

        public ExecuteRunnable(ExecuteTask executeTask){
            this.executeTask = executeTask;
        }

        @Override
        public void run() {
            executeTask.execute();
        }
    }
}

package com.joe.im.util;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.util.concurrent.*;

public class JsonUtil {
    private static GsonBuilder gb = new GsonBuilder();

    private static final Gson gson;

    static {
        gb.disableHtmlEscaping();
        gson = gb.create();
    }

    public static byte[] object2JsonBytes(Object o){
        return pojo2Json(o).getBytes(Charsets.UTF_8);
    }


    public static <T> T jsonBytes2Object(byte[] bytes,Class<T> tClass){
        String s = new String(bytes, Charsets.UTF_8);
        return json2Pojo(s, tClass);
    }

    public static <T> T json2Pojo(String json,Class<T> tClass){
        return gson.fromJson(json, tClass);
    }

    public static String pojo2Json(Object o){
        return gson.toJson(o);
    }


    public static void main(String[] args) {
        // 构造线程池
        EventExecutor executor = new DefaultEventExecutor();

        // 创建 DefaultPromise 实例
        Promise promise = new DefaultPromise(executor);

        // 下面给这个 promise 添加两个 listener
        promise.addListener((GenericFutureListener<Future<Integer>>) future -> {
            if (future.isSuccess()) {
                System.out.println("任务结束，结果：" + future.get());
            } else {
                System.out.println("任务失败，异常：" + future.cause());
            }
        }).addListener((GenericFutureListener<Future<Integer>>) future -> System.out.println("任务结束，balabala..."));

        // 提交任务到线程池，五秒后执行结束，设置执行 promise 的结果
        executor.submit(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            // 设置 promise 的结果
            promise.setFailure(new RuntimeException());
//                promise.setSuccess(123456);
        });

        // main 线程阻塞等待执行结果
        try {
            //promise.sync();
            promise.await();
        } catch (InterruptedException e) {
        }
    }
}

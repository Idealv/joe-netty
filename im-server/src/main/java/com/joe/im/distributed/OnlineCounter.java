package com.joe.im.distributed;

import com.joe.im.constant.ServerConstant;
import com.joe.im.zookeeper.ZKClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.RetryNTimes;

@Slf4j
public class OnlineCounter {

    private CuratorFramework client;

    private DistributedAtomicLong onlines = null;

    public static OnlineCounter getInstance(){
        return Singleton.INSTANCE.getInstance();
    }

    private OnlineCounter(){}

    private enum Singleton{
        INSTANCE;

        private OnlineCounter instance;

        Singleton(){
            instance = new OnlineCounter();
            instance.client = ZKClient.instance.getClient();
            instance.init();
        }

        public OnlineCounter getInstance() {
            return instance;
        }
    }

    private void init(){
        onlines = new DistributedAtomicLong(client, ServerConstant.COUNTER_PREFIX, new RetryNTimes(10, 30));
    }

    public boolean incr(){
        boolean result = false;
        AtomicValue<Long> val = null;

        try {
            val=onlines.increment();
            result = val.succeeded();
            log.info("old count={},new count={},result={}", val.preValue(), val.postValue(), result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean decr(){
        boolean result = false;
        AtomicValue<Long> val = null;

        try {
            val = onlines.decrement();
            result = val.succeeded();
            log.info("old count={},new count={},result={}", val.preValue(), val.postValue(), result);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }
}

package com.joe.im.zookeeper.distributedLock;

import com.joe.im.concurrent.FutureTaskScheduler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class ZKLockTest {
    int count=0;

    @Test
    public void testLock() throws Exception{
        for (int i = 0; i < 10; i++) {
            FutureTaskScheduler.add(()->{
                ZKLock lock = new ZKLock();
                lock.lock();
                for (int j = 0; j < 10; j++) {
                    count++;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("count={}", count);
                lock.unlock();
            });
        }

        Thread.sleep(Integer.MAX_VALUE);
    }
}
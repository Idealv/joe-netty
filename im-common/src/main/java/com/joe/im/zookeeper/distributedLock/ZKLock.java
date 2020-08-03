package com.joe.im.zookeeper.distributedLock;

import com.joe.im.zookeeper.ZKClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.Watcher;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ZKLock implements Lock {

    private static final String ZK_PATH = "/test/lock";

    private static final String LOCK_PREFIX = ZK_PATH + "/";

    private static final long WAIT_TIME = 1000;

    CuratorFramework client = null;

    private String locked_short_path = null;

    private String locked_path = null;

    private String prior_path = null;

    final AtomicInteger count = new AtomicInteger(0);

    private Thread thread;

    public ZKLock() {
        if (!ZKClient.instance.isNodeExist(ZK_PATH)) {
            ZKClient.instance.createNode(ZK_PATH, null);
        }
        client = ZKClient.instance.getClient();
    }

    @Override
    public boolean lock() {
        synchronized (this) {
            if (count.get() == 0) {
                thread = Thread.currentThread();
                count.incrementAndGet();
            } else {
                if (!thread.equals(Thread.currentThread())) {
                    return false;
                }
                count.incrementAndGet();
                return true;
            }
        }

        try {
            boolean locked = false;

            locked = tryLock();

            if (locked) return true;

            while (!locked) {
                await();
                List<String> waiters = getWaiters();
                if (checkLocked(waiters)) locked = true;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean unlock() {
        if (!thread.equals(Thread.currentThread())) return false;

        int newLockCount = count.decrementAndGet();

        if (newLockCount < 0) throw new IllegalMonitorStateException("可重入锁计数错误: " + locked_path);

        if (newLockCount != 0) return true;

        try {
            if (ZKClient.instance.isNodeExist(locked_path)) client.delete().forPath(locked_path);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    public List<String> getWaiters() {
        List<String> children = null;

        try {
            children = client.getChildren().forPath(ZK_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return children;
    }

    private boolean checkLocked(List<String> waiters) {
        Collections.sort(waiters);

        if (locked_short_path.equals(waiters.get(0))) {
            log.info("获取分布式锁成功,节点为:{}", locked_short_path);
            return true;
        }

        return false;
    }

    private void await() {
        if (null == prior_path) throw new RuntimeException("prior_path error");

        final CountDownLatch latch = new CountDownLatch(1);

        try {
            client.getData().usingWatcher((Watcher) watchedEvent -> {
                log.info("监听到的变化:watchedEvent{}", watchedEvent);
                log.info("[WatchedEvent]节点删除");
                latch.countDown();
            }).forPath(prior_path);

            latch.await(WAIT_TIME, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean tryLock() {
        locked_path = ZKClient.instance.createEphemeralNode(LOCK_PREFIX);
        if (null == locked_path) throw new RuntimeException("zk error");

        locked_short_path = getShortPath(locked_path);

        List<String> waiters = getWaiters();

        if (checkLocked(waiters)) {
            return true;
        }

        int index = Collections.binarySearch(waiters, locked_short_path);

        if (index < 0) throw new RuntimeException("节点:" + locked_short_path + "没有找到");

        prior_path = ZK_PATH + "/" + waiters.get(index - 1);

        return false;
    }

    private String getShortPath(String lockedPath) {
        int index = lockedPath.lastIndexOf("/");
        if (index > -1) {
            return index <= lockedPath.length() ? lockedPath.substring(index) : "";
        }
        return null;
    }
}

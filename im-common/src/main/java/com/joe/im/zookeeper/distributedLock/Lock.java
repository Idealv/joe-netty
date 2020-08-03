package com.joe.im.zookeeper.distributedLock;

public interface Lock {
    boolean lock();

    boolean unlock();
}

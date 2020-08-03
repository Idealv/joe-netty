package com.joe.im.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ClientFactory {
    public static CuratorFramework createSimple(String connectString){
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        return CuratorFrameworkFactory.newClient(connectString, retryPolicy);
    }

    public static CuratorFramework createWithOptions(String connectString, RetryPolicy retryPolicy,
                                                     int connectionTimeOutMs,int sessionTimeOutMs){
        return CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(connectionTimeOutMs)
                .sessionTimeoutMs(sessionTimeOutMs)
                .build();
    }
}

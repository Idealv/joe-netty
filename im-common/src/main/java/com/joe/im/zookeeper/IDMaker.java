package com.joe.im.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;

public class IDMaker {
    private static final String ZK_ADDRESS = "127.0.0.1:2181";

    CuratorFramework client = null;

    public void init(){
        client = ClientFactory.createSimple(ZK_ADDRESS);
        client.start();
    }

    private String createSeqNode(String pathPrefix) {
        try {
            String destPath = client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(pathPrefix);
            return destPath;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String makeId(String nodeName){
        String str = createSeqNode(nodeName);
        if (null==str)return null;

        int idx = str.lastIndexOf(nodeName);
        if (idx>0){
            idx += nodeName.length();
            return idx <= str.length() ? str.substring(idx) : "";
        }
        return str;
    }

    public void destory(){
        CloseableUtils.closeQuietly(client);
    }
}

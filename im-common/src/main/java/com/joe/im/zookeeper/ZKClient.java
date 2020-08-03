package com.joe.im.zookeeper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.joe.im.common.bean.Demo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

@Slf4j
public class ZKClient {

    private CuratorFramework client;

    private static final String ZK_ADDRESS = "127.0.0.1:2181";

    public static ZKClient instance = null;

    static {
        instance = new ZKClient();
        instance.init();
    }

    private ZKClient() {

    }

    public CuratorFramework getClient() {
        return client;
    }

    public void init(){
        if (null!=client)return;

        client = ClientFactory.createSimple(ZK_ADDRESS);
        client.start();
    }

    public void destory(){
        CloseableUtils.closeQuietly(client);
    }

    public void createNode(String nodePath,String data){
        byte[] payload = data != null ? data.getBytes(Charsets.UTF_8) : "to set content".getBytes(Charsets.UTF_8);
        try {
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(nodePath, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteNode(String nodePath){
        if (!isNodeExist(nodePath)){
            return;
        }
        try {
            client.delete().forPath(nodePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String createEphemeralNode(String nodePath){
        try {
            String data = client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(nodePath);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isNodeExist(String nodePath){
        try {
            Stat stat = client.checkExists().forPath(nodePath);
            if (null==stat){
                log.info("节点不存在:{}", nodePath);
                return false;
            }else {
                log.info("节点存在:{}", stat.toString());
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        Demo demo = new Demo();
        demo.setId(1l);
        demo.setContent("Hello,World!");
    }
}

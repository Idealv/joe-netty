package com.joe.im.distributed;

import com.joe.im.constant.ServerConstant;
import com.joe.im.util.JsonUtil;
import com.joe.im.zookeeper.ZKClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class ImWorker {
    private CuratorFramework client;

    private String pathRegistered;

    private ImNode node = ImNode.getInstance();

    public static ImWorker getInstance(){
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton{
        INSTANCE;

        private ImWorker instance;

        Singleton(){
            instance = new ImWorker();
            instance.client = ZKClient.instance.getClient();
            instance.init();
        }

        public ImWorker getInstance() {
            return instance;
        }
    }

    public void init(){
        createParentIfNeeded(ServerConstant.MANAGE_PATH);

        byte[] payload = JsonUtil.object2JsonBytes(node);

        try {
            pathRegistered = client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(ServerConstant.PATH_PREFIX, payload);
            node.setId(getId(ServerConstant.PATH_PREFIX));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean incrBalance(){
        node.getBalanace().incrementAndGet();
        byte[] payload = JsonUtil.object2JsonBytes(node);
        try {
            client.setData().forPath(pathRegistered, payload);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean decrBalance(){
        node.getBalanace().decrementAndGet();

        if (node.getBalanace().get()<0) node.getBalanace().set(0);

        byte[] paylaod = JsonUtil.object2JsonBytes(node);
        try {
            client.setData().forPath(pathRegistered, paylaod);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public long getId(String path){
        String sid = null;
        if (null==pathRegistered) throw new RuntimeException("节点注册失败");
        int idx = pathRegistered.lastIndexOf(path);
        if (idx>=0){
            idx += ServerConstant.PATH_PREFIX.length();
            sid = idx <= pathRegistered.length() ? pathRegistered.substring(idx) : null;
        }
        if (sid==null) throw new RuntimeException("节点Id生成失败");

        return Long.parseLong(sid);
    }

    private void createParentIfNeeded(String managePath){
        try {
            Stat stat = client.checkExists().forPath(managePath);
            if (null==stat){
                client.create()
                        .creatingParentsIfNeeded()
                        .withProtection()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(managePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

package com.joe.im.distributed;

import com.google.common.io.LittleEndianDataInputStream;
import com.joe.im.constant.ServerConstant;
import com.joe.im.util.JsonUtil;
import com.joe.im.zookeeper.ZKClient;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class ImLoadBalance {

    private CuratorFramework client;

    public ImLoadBalance(){
        client = ZKClient.instance.getClient();
    }

    public static ImLoadBalance getInstance(){
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton{
        INSTANCE;

        private ImLoadBalance instance;

        Singleton(){
            instance = new ImLoadBalance();
        }

        public ImLoadBalance getInstance(){
            return instance;
        }
    }

    public ImNode getBestWorker(){
        return balance(getWorkers());
    }

    private ImNode balance(List<ImNode> workers){
        if (workers.size()>0) {
            Collections.sort(workers);
            return workers.get(0);
        }
        return null;
    }

    private List<ImNode> getWorkers(){
        List<ImNode> workers = new ArrayList<>();
        List<String> children = null;
        try {
            children = client.getChildren().forPath(ServerConstant.MANAGE_PATH);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        for (String child:
             children) {
            log.info("child:{}", child);
            byte[] payload=null;
            try {
                payload = client.getData().forPath(child);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null==payload)continue;

            ImNode worker = JsonUtil.jsonBytes2Object(payload, ImNode.class);
            workers.add(worker);
        }

        return workers;
    }
}

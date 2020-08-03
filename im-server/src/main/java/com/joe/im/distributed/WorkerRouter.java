package com.joe.im.distributed;

import com.joe.im.constant.ServerConstant;
import com.joe.im.util.JsonUtil;
import com.joe.im.zookeeper.ZKClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WorkerRouter {

    private CuratorFramework client;

    private Map<Long, WorkerReSender> workerMap = new ConcurrentHashMap<>();

    public static WorkerRouter getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton{
        INSTANCE;

        private WorkerRouter instance;

        Singleton(){
            instance = new WorkerRouter();
            instance.client = ZKClient.instance.getClient();
            instance.init();
        }

        public WorkerRouter getInstance() {
            return instance;
        }
    }

    private WorkerRouter(){}

    private void init(){
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, ServerConstant.MANAGE_PATH, true);
        pathChildrenCache.getListenable().addListener((client, event) -> {
            log.info("开始监听其它ImWorker子节点");
            ChildData data = event.getData();
            switch (event.getType()){
                case CHILD_ADDED:
                    log.info("CHILD_ADDED: path:{},  data:{}", data.getPath(), data.getData());
                    break;
                case CHILD_REMOVED:
                    log.info("CHILD_REMOVED: path:{},  data:{}", data.getPath(), data.getData());
                    break;
                case CHILD_UPDATED:
                    log.info("CHILD_UPDATED: path:{},  data:{}", data.getPath(), data.getData());
                    break;
                default:
                    log.debug("[PathChildrenCache]节点数据为空,path:{}", data == null ? "null" : data.getPath());
                    break;
            }
        });
    }

    private void processNodeAdded(ChildData data){
        byte[] payload = data.getData();
        String path = data.getPath();
        log.info("[PathChildrenCache]添加新节点: path:{}, data:{}", path, payload);
        ImNode node = JsonUtil.jsonBytes2Object(payload, ImNode.class);
        long id=ImWorker.getInstance().getId(path);
        node.setId(id);

        WorkerReSender workerReSender = workerMap.get(id);
        //为本地节点
        if (null!=workerReSender&&workerReSender.getRemoteNode().equals(node))return;

        workerReSender = new WorkerReSender(node);

        workerMap.put(id, workerReSender);
    }

    public void remove(ImNode node){
        workerMap.remove(node.getId());
    }
}

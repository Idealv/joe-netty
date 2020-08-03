package com.joe.im.zookeeper;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.util.List;

@Slf4j
public class ClientFactoryTest {
    private static final String ZK_ADDRESS = "127.0.0.1:2181";

    @Test
    public void testCreateSimple() {
        CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);

        try {
            client.start();

            String data = "hello";
            byte[] payload = data.getBytes(Charsets.UTF_8);
            String zkPath="/test/CRUD/node-1";
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(zkPath, payload);
        }catch (Exception e) {
            log.error(e.getMessage());
        }finally {
            CloseableUtils.closeQuietly(client);
        }
    }

    @Test
    public void testReadNode(){
        CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
        try {
            client.start();
            String zkPath = "/test/CRUD/node-1";
            Stat stat = client.checkExists().forPath(zkPath);
            if (null!=stat){
                byte[] payload = client.getData().forPath(zkPath);
                String data = new String(payload, Charsets.UTF_8);
                log.info("read data:{} from node:{}", data, zkPath);

                String parentPath="/test";
                List<String> children = client.getChildren().forPath(parentPath);
                for (String child:
                     children) {
                    log.info("child: {}", child);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }

    @Test
    public void testDelete() {
        CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);

        try {
            client.start();
            String zkPath = "/test/CRUD/node-1";
            client.delete().forPath(zkPath);

            String parentPath = "/test";
            List<String> children = client.getChildren().forPath(parentPath);
            for (String child:
                 children) {
                log.info("child:{}", child);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }
}
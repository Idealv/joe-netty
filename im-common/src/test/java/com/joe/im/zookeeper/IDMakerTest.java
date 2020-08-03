package com.joe.im.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class IDMakerTest {

    @Test
    public void makeId() {
        IDMaker idMaker = new IDMaker();
        idMaker.init();
        String nodeName = "/test/IDMaker/ID-";

        for (int i = 0; i < 10; i++) {
            String id = idMaker.makeId(nodeName);
            log.info("第{}个创建的id为:{}", i, id);
        }
        idMaker.destory();
    }
}
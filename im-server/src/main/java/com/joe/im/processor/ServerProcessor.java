package com.joe.im.processor;

import com.joe.im.common.bean.msg.ProtoMsg;
import com.joe.im.server.ServerSession;

public interface ServerProcessor {

    ProtoMsg.HeadType type();

    boolean action(ServerSession session, ProtoMsg.Message proto);
}


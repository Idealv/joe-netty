package com.joe.im.builder;

import com.joe.im.client.ClientSession;
import com.joe.im.common.bean.msg.ProtoMsg;

public abstract class BaseBuilder {
    protected ProtoMsg.HeadType type;
    private long seqId;
    private ClientSession session;

    public BaseBuilder(ProtoMsg.HeadType type,ClientSession session){
        this.type = type;
        this.session = session;
    }

    public ProtoMsg.Message buildCommon(long seqId){
        this.seqId = seqId;
        ProtoMsg.Message.Builder builder = ProtoMsg.Message.newBuilder()
                .setType(type)
                .setSessionId(session.getSessionId())
                .setSequence(this.seqId);
        return builder.buildPartial();
    }
}

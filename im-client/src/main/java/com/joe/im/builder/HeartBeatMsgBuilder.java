package com.joe.im.builder;

import com.joe.im.client.ClientSession;
import com.joe.im.common.bean.User;
import com.joe.im.common.bean.msg.ProtoMsg;

public class HeartBeatMsgBuilder extends BaseBuilder {
    private final User user;

    public HeartBeatMsgBuilder(User user, ClientSession session) {
        super(ProtoMsg.HeadType.HEART_BEAT, session);
        this.user = user;
    }

    public ProtoMsg.Message buildMsg(){
        ProtoMsg.Message message = buildCommon(-1);
        ProtoMsg.MessageHeartBeat.Builder hb = ProtoMsg.MessageHeartBeat.newBuilder()
                .setSeq(0)
                .setUid(user.getUid())
                .setJson("{\"from\":\"client\"}");
        return message.toBuilder().setMessageHeartBeat(hb).buildPartial();
    }
}

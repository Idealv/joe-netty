package com.joe.im.builder;

import com.joe.im.client.ClientSession;
import com.joe.im.common.bean.ChatMsg;
import com.joe.im.common.bean.User;
import com.joe.im.common.bean.msg.ProtoMsg;

public class ChatMsgBuilder extends BaseBuilder {

    private ChatMsg chatMsg;

    private User user;

    public ChatMsgBuilder(ChatMsg chatMsg,User user, ClientSession session) {
        super(ProtoMsg.HeadType.MESSAGE_REQUEST, session);
        this.chatMsg = chatMsg;
        this.user = user;
    }

    private ProtoMsg.Message build(){
        ProtoMsg.Message message = buildCommon(-1);

        ProtoMsg.MessageRequest.Builder cb = ProtoMsg.MessageRequest.newBuilder();
        chatMsg.fillMsg(cb);
        return message.toBuilder()
                .setMessageRequest(cb.buildPartial())
                .buildPartial();
    }

    public static ProtoMsg.Message build(ChatMsg chatMsg,User user,ClientSession session){
        ChatMsgBuilder builder = new ChatMsgBuilder(chatMsg, user, session);
        return builder.build();
    }
}

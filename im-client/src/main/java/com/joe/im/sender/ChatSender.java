package com.joe.im.sender;

import com.joe.im.builder.ChatMsgBuilder;
import com.joe.im.common.bean.ChatMsg;
import com.joe.im.common.bean.msg.ProtoMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatSender extends BaseSender {

    public void sendMsg(String toUid,String content){
        ChatMsg chatMsg = new ChatMsg(getUser());
        chatMsg.setContent(content);
        chatMsg.setTo(toUid);
        chatMsg.setMsgType(ChatMsg.MSGTYPE.TEXT);
        chatMsg.setMsgId(System.currentTimeMillis());
        ProtoMsg.Message message = ChatMsgBuilder.build(chatMsg, getUser(), getSession());
        super.sendMsg(message);
    }

    @Override
    protected void sendSucess(ProtoMsg.Message message) {
        log.info("发送成功: {}", message.getMessageRequest().getContent());
    }

    @Override
    protected void sendFailed(ProtoMsg.Message message) {
        log.info("发送失败: {}", message.getMessageRequest().getContent());
    }
}

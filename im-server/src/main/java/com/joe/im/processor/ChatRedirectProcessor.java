package com.joe.im.processor;

import com.joe.im.common.bean.msg.ProtoMsg;
import com.joe.im.server.ServerSession;
import com.joe.im.server.SessionMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("chatRedirectProcessor")
@Slf4j
public class ChatRedirectProcessor extends AbstractProcessor{
    @Override
    public ProtoMsg.HeadType type() {
        return ProtoMsg.HeadType.MESSAGE_REQUEST;
    }

    @Override
    public boolean action(ServerSession session, ProtoMsg.Message proto) {
        ProtoMsg.MessageRequest request = proto.getMessageRequest();
        log.info("chatMsg | from:{} ,to:{} , content:{}", request.getFrom(), request.getTo(), request.getContent());
        String to = request.getTo();
        List<ServerSession> toSessions = SessionMap.getInstance().getSessionByUid(to);
        if (toSessions==null){
            log.error("[ {} ] 不在线,信息发送失败", to);
        }else {
            toSessions.forEach(s -> s.writeAndFlush(proto));
        }
        return true;
    }
}

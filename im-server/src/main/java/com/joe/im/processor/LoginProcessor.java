package com.joe.im.processor;

import com.joe.im.builder.LoginResponseBuilder;
import com.joe.im.common.ProtoInstant;
import com.joe.im.common.bean.User;
import com.joe.im.common.bean.msg.ProtoMsg;
import com.joe.im.server.ServerSession;
import com.joe.im.server.SessionMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoginProcessor extends AbstractProcessor {
    @Autowired
    private LoginResponseBuilder loginResponseBuilder;

    @Override
    public ProtoMsg.HeadType type() {
        return ProtoMsg.HeadType.LOGIN_REQUEST;
    }

    @Override
    public boolean action(ServerSession session, ProtoMsg.Message proto) {
        ProtoMsg.LoginRequest info = proto.getLoginRequest();
        long seqId = proto.getSequence();
        User user = User.fromMsg(info);
        boolean isValidUser = checkUser(user);
        if (!isValidUser) {
            log.info("用户登录失败: uid:{}", user.getUid());
            ProtoInstant.ResultCodeEnum resultCode = ProtoInstant.ResultCodeEnum.NO_TOKEN;
            ProtoMsg.Message response = loginResponseBuilder.build(resultCode, seqId, "-1");
            session.writeAndFlush(response);
            return false;
        }

        session.setUser(user);
        session.bind();

        ProtoInstant.ResultCodeEnum resultCode = ProtoInstant.ResultCodeEnum.SUCCESS;
        ProtoMsg.Message response = loginResponseBuilder.build(resultCode, seqId, session.getSessionId());
        session.writeAndFlush(response);
        return true;
    }

    private boolean checkUser(User user) {
        //todo 使用redis优化性能 键值对user:uid:platform
//        if (!SessionMap.getInstance().hasLogin(user)) {
////            return false;
////        }
        return true;
    }
}

package com.joe.im.builder;

import com.joe.im.common.ProtoInstant;
import com.joe.im.common.bean.msg.ProtoMsg;
import org.springframework.stereotype.Service;

@Service
public class LoginResponseBuilder {
    public ProtoMsg.Message build(ProtoInstant.ResultCodeEnum en, long seqId, String sessionId) {
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.LOGIN_RESPONSE)
                .setSessionId(sessionId)
                .setSequence(seqId);
        ProtoMsg.LoginResponse loginResponse = ProtoMsg.LoginResponse.newBuilder()
                .setCode(en.getCode())
                .setInfo(en.getDesc())
                .setExpose(1)
                .build();
        mb.setLoginResponse(loginResponse);
        return mb.buildPartial();
    }
}

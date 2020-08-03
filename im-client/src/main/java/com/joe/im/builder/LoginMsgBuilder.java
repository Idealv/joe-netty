package com.joe.im.builder;

import com.joe.im.client.ClientSession;
import com.joe.im.common.bean.User;
import com.joe.im.common.bean.msg.ProtoMsg;

public class LoginMsgBuilder extends BaseBuilder{
    private User user;

    public LoginMsgBuilder(User user, ClientSession session) {
        super(ProtoMsg.HeadType.LOGIN_REQUEST, session);
        this.user = user;
    }

    public ProtoMsg.Message build(){
        ProtoMsg.Message message = buildCommon(-1);
        ProtoMsg.LoginRequest lb = ProtoMsg.LoginRequest.newBuilder()
                .setDeviceId(user.getDevId())
                .setPlatform(user.getPlatform().ordinal())
                .setToken(user.getToken())
                .setUid(user.getUid()).buildPartial();
        return message.toBuilder().setLoginRequest(lb).buildPartial();
    }

    public static ProtoMsg.Message buildLoginMsg(User user,ClientSession session){
        LoginMsgBuilder builder = new LoginMsgBuilder(user, session);
        return builder.build();
    }
}

package com.joe.im.sender;

import com.joe.im.builder.LoginMsgBuilder;
import com.joe.im.common.bean.msg.ProtoMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("loginSender")
public class LoginSender extends BaseSender{
    public void sendLoginMsg(){
        if (!isConnected()){
            log.info("连接尚未建立");
            return;
        }
        log.info("生成登录信息");
        ProtoMsg.Message message = LoginMsgBuilder.buildLoginMsg(getUser(), getSession());
        log.info("发送登录信息");
        super.sendMsg(message);
    }
}

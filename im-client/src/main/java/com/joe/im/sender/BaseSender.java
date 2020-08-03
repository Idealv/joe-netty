package com.joe.im.sender;

import com.joe.im.client.ClientSession;
import com.joe.im.common.bean.User;
import com.joe.im.common.bean.msg.ProtoMsg;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@ToString
@Slf4j
public abstract class BaseSender {
    private User user;

    private ClientSession session;

    public boolean isConnected(){
        if (null==session){
            log.info("session is null");
            return false;
        }
        return session.isConnected();
    }

    public boolean isLogin(){
        if (null==session){
            log.info("session is null");
            return false;
        }
        return session.isLogin();
    }

    public void sendMsg(ProtoMsg.Message message){
        if (!isConnected()||null==session){
            log.info("连接尚未建立!");
            return;
        }
        Channel channel = session.getChannel();
        ChannelFuture f = channel.writeAndFlush(message);
        f.addListener(future -> {
            if (future.isSuccess()) sendSucess(message);
            else sendFailed(message);
        });
    }

    protected void sendSucess(ProtoMsg.Message message){
        log.info("发送成功");
    }

    protected void sendFailed(ProtoMsg.Message message){
        log.info("发送失败");
    }
}

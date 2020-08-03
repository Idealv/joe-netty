package com.joe.im.client;

import com.joe.im.common.bean.User;
import com.joe.im.common.bean.msg.ProtoMsg;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
@Getter@Setter
public class ClientSession {
    public static final AttributeKey<ClientSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");

    private Channel channel;

    private User user;

    private String sessionId;

    private boolean isConnected = false;

    private boolean isLogin = false;

    public ClientSession(Channel channel){
        this.channel = channel;
        this.sessionId = String.valueOf(-1);
        channel.attr(SESSION_KEY).set(this);
    }

    public static void loginSuccess(ChannelHandlerContext ctx, ProtoMsg.Message msg){
        Channel channel = ctx.channel();
        ClientSession session = channel.attr(SESSION_KEY).get();
        session.setSessionId(msg.getSessionId());
        session.setLogin(true);
        log.info("登录成功!");
    }

    public static ClientSession getSession(ChannelHandlerContext ctx){
        Channel channel = ctx.channel();
        return channel.attr(SESSION_KEY).get();
    }

    public String gerRemoteAddress(){
        return channel.remoteAddress().toString();
    }

    public ChannelFuture writeAndFlush(Object pkg){
        ChannelFuture channelFuture = channel.writeAndFlush(pkg);
        return channelFuture;
    }

    public void writeAndClose(Object pkg){
        ChannelFuture channelFuture = channel.writeAndFlush(pkg);
        channelFuture.addListener(ChannelFutureListener.CLOSE);
    }

    public void close(){
        isConnected = false;
        ChannelFuture future = channel.close();
        future.addListener(listener->{
            if (listener.isSuccess()){
                log.info("连接成功断开!");
            }
        });
    }
}

package com.joe.im.server;

import com.joe.im.common.bean.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Getter@Setter
@NoArgsConstructor
public class ServerSession {
    public static final AttributeKey<ServerSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");
    public static final AttributeKey<String> KEY_USER_ID = AttributeKey.valueOf("KEY_USER_ID");

    private Channel channel;

    private User user;

    private String sessionId;

    private boolean isLogin=false;

    public ServerSession(Channel channel){
        this.channel = channel;
        this.sessionId = buildSessionId();
    }

    public static ServerSession getSession(ChannelHandlerContext ctx){
        return ctx.channel().attr(SESSION_KEY).get();
    }

    public static void closeSession(ChannelHandlerContext ctx){
        ServerSession session = ctx.channel().attr(SESSION_KEY).get();
        if (null!=session&&session.isValid()){
            session.close();
            SessionMap.getInstance().removeSession(session.getSessionId());
        }
    }

    public ServerSession bind(){
        log.info("ServerSession绑定会话: {}", channel.remoteAddress());
        channel.attr(SESSION_KEY).set(this);
        SessionMap.getInstance().addSession(getSessionId(), this);
        isLogin = true;
        return this;
    }

    public ServerSession unbind() {
        isLogin = false;
        SessionMap.getInstance().removeSession(sessionId);
        close();
        return this;
    }

    public synchronized void writeAndFlush(Object pkg){
        channel.writeAndFlush(pkg);
    }



    private String buildSessionId(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public boolean isValid(){
        return user != null ? true : false;
    }

    public void setUser(User user) {
        this.user = user;
        this.user.setSessionId(sessionId);
    }

    public synchronized void close(){
        ChannelFuture future = channel.close();
        future.addListener(listener->{
            if (!listener.isSuccess()){
                log.info("channel close failed:{}",listener.cause().getMessage());
            }
        });
    }
}

package com.joe.im.processor;

import com.joe.im.server.ServerSession;
import io.netty.channel.Channel;

public abstract class AbstractProcessor implements ServerProcessor {
    protected String getKey(Channel channel){
        return channel.attr(ServerSession.KEY_USER_ID).get();
    }

    protected void setKey(Channel channel,String uid){
        channel.attr(ServerSession.KEY_USER_ID).set(uid);
    }

    protected void checkAuth(Channel channel) throws RuntimeException{
        if (null==getKey(channel)) throw new RuntimeException("该用户未登录!");
    }
}

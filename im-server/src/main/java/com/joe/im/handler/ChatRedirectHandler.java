package com.joe.im.handler;

import com.joe.im.common.bean.msg.ProtoMsg;
import com.joe.im.concurrent.FutureTaskScheduler;
import com.joe.im.processor.AbstractProcessor;
import com.joe.im.processor.ChatRedirectProcessor;
import com.joe.im.server.ServerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ChannelHandler.Sharable
public class ChatRedirectHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private AbstractProcessor chatRedirectProcessor;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null==msg||!(msg instanceof ProtoMsg.Message)){
            super.channelRead(ctx, msg);
            return;
        }
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType headType = pkg.getType();

        if (!headType.equals(chatRedirectProcessor.type())) {
            super.channelRead(ctx, msg);
            return;
        }

        ServerSession session = ServerSession.getSession(ctx);
        if (null==session||!session.isLogin()){
            log.error("用户尚未登录，不能发送消息!");
            return;
        }

        FutureTaskScheduler.add(() -> chatRedirectProcessor.action(session, pkg));
    }
}

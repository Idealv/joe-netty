package com.joe.im.handler;

import com.joe.im.common.bean.msg.ProtoMsg;
import com.joe.im.concurrent.CallbackTask;
import com.joe.im.concurrent.CallbackTaskScheduler;
import com.joe.im.processor.LoginProcessor;
import com.joe.im.server.ServerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("loginRequestHandler")
@ChannelHandler.Sharable
public class LoginRequestHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private LoginProcessor loginProcessor;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMsg.Message message = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType type = message.getType();
        if (!type.equals(loginProcessor.type())){
            super.channelRead(ctx, msg);
            return;
        }

        ServerSession session = new ServerSession(ctx.channel());
        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {

            @Override
            public Boolean execute() throws Exception {
                return loginProcessor.action(session, message);
            }

            @Override
            public void onBack(Boolean r) {
                if (r){
                    ctx.pipeline().remove(LoginRequestHandler.this);
                    log.info("登录成功: {}", session.getUser());
                }else {
                    ServerSession.closeSession(ctx);
                    log.info("登录失败: {}", session.getUser());
                }
            }

            @Override
            public void onException(Throwable t) {
                ServerSession.closeSession(ctx);
                log.info("登录失败: {}", session.getUser());
            }
        });

    }
}

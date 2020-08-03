package com.joe.im.handler;

import com.joe.im.common.bean.msg.ProtoMsg;
import com.joe.im.concurrent.FutureTaskScheduler;
import com.joe.im.server.ServerSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class HeartBeatServerHandler extends IdleStateHandler {
    private static final int READ_IDLE_GAP = 150;

    public HeartBeatServerHandler() {
        super(READ_IDLE_GAP, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        log.info("{} 秒内未读到数据，关闭连接", READ_IDLE_GAP);
        ServerSession.closeSession(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null==msg||!(msg instanceof ProtoMsg.Message)){
            super.channelRead(ctx, msg);
            return;
        }
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        if (pkg.getType().equals(ProtoMsg.HeadType.HEART_BEAT)){
            FutureTaskScheduler.add(()->{
                if (ctx.channel().isActive()){
                    ctx.writeAndFlush(msg);
                }
            });
        }
        super.channelRead(ctx, msg);
    }
}

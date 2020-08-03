package com.joe.im.handler;

import com.joe.im.builder.HeartBeatMsgBuilder;
import com.joe.im.client.ClientSession;
import com.joe.im.common.bean.User;
import com.joe.im.common.bean.msg.ProtoMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@ChannelHandler.Sharable
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {
    private static final int HEARTBEAT_INTERVAL = 150;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ClientSession session = ClientSession.getSession(ctx);
        User user = session.getUser();
        HeartBeatMsgBuilder hb = new HeartBeatMsgBuilder(user, session);
        ProtoMsg.Message message = hb.buildMsg();
        heartBeat(ctx, message);
    }

    public void heartBeat(ChannelHandlerContext ctx, ProtoMsg.Message heartBeatMsg){
        ctx.executor().schedule(() -> {
            if (ctx.channel().isActive()){
                log.info("发送heartBeat数据包到服务端");
                ctx.writeAndFlush(heartBeatMsg);
                heartBeat(ctx, heartBeatMsg);
            }
        }, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        if (pkg.getType().equals(ProtoMsg.HeadType.HEART_BEAT)){
            log.info("收到服务端回写的HEART_BEAT消息");
            return;
        }else {
            super.channelRead(ctx,msg);
        }
    }
}

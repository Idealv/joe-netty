package com.joe.im.handler;

import com.joe.im.client.ClientSession;
import com.joe.im.common.ProtoInstant;
import com.joe.im.common.bean.msg.ProtoMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("loginResponseHandler")
@ChannelHandler.Sharable
@Slf4j
public class LoginResponseHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    HeartBeatClientHandler heartBeatClientHandler;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null==msg||!(msg instanceof ProtoMsg.Message)){
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType type = pkg.getType();
        if (!type.equals(ProtoMsg.HeadType.LOGIN_RESPONSE)){
            super.channelRead(ctx, msg);
            return;
        }
        ProtoMsg.LoginResponse info = pkg.getLoginResponse();
        ProtoInstant.ResultCodeEnum resultCode = ProtoInstant.ResultCodeEnum.values()[info.getCode()];

        if (!resultCode.equals(ProtoInstant.ResultCodeEnum.SUCCESS)){
            log.info(resultCode.getDesc());
        }else {
            ClientSession.loginSuccess(ctx, pkg);
            ctx.pipeline().remove(this);
            log.info("remove LoginResponseHandler");
            ctx.pipeline().addAfter("encoder", "heartBeat", heartBeatClientHandler);
            log.info("add HeartBeatClientHandler after encoder");
        }
    }
}

package com.joe.im.common.codec;

import com.joe.im.common.ProtoInstant;
import com.joe.im.common.bean.msg.ProtoMsg;
import com.joe.im.common.exception.InvalidFrameException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.springframework.stereotype.Service;

import javax.xml.crypto.dsig.spec.HMACParameterSpec;
import java.util.List;

public class ProtobufDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        //魔数 版本 长度
        //2   2    4
        if (in.readableBytes()<8){
            return;
        }
        short magicNumber = in.readShort();
        if (magicNumber!= ProtoInstant.MAGIC_CODE){
            throw new InvalidFrameException("客户端口令不正确:" + ctx.channel().remoteAddress());
        }
        short version = in.readShort();


        int len = in.readShort();
        if (len<0){
            ctx.close();
        }else if (len>in.readableBytes()){
            in.resetReaderIndex();
            return;
        }

        byte[] bytes = new byte[len];
        in.readBytes(bytes, 0, len);
        ProtoMsg.Message message = ProtoMsg.Message.parseFrom(bytes);
        if (message!=null){
            out.add(message);
        }
    }
}

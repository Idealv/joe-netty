package com.joe.im;

import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.nio.channels.Channels;

@Slf4j
public class ContextTest {

    @ChannelHandler.Sharable
    private class FactorialHandler extends ChannelInboundHandlerAdapter {

        private final AttributeKey<Integer> counter = AttributeKey.valueOf("counter");

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Integer o = ctx.channel().attr(counter).get();

            if (o==null)o=1;

            ctx.channel().attr(counter).set((Integer) msg * o);
            super.channelRead(ctx, ctx.channel().attr(counter).get() + 1);
        }
    }

    @Test
    public void test() throws Exception{
        FactorialHandler fh = new FactorialHandler();
        EmbeddedChannel ch = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ChannelPipeline p1 = ch.pipeline();
                p1.addLast("f1", fh);
                p1.addLast("f2", fh);

                ChannelPipeline p2 = ch.pipeline();
                p2.addLast("f3", fh);
                p2.addLast("f4", fh);
            }
        });
        ch.writeInbound(2);
        ch.flush();

        //Thread.sleep(Integer.MAX_VALUE);
    }
}

package com.joe.im.client;

import com.joe.im.common.codec.ProtobufDecoder;
import com.joe.im.common.codec.ProtobufEncoder;
import com.joe.im.handler.ChatMsgHandler;
import com.joe.im.handler.ExceptionHandler;
import com.joe.im.handler.LoginResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Getter@Setter
@Slf4j
public class ImClient {
    @Value("${server.ip}")
    public String host;

    @Value("${server.port}")
    public int port;

    @Autowired
    private LoginResponseHandler loginResponseHandler;

    @Autowired
    private ChatMsgHandler chatMsgHandler;

    @Autowired
    private ExceptionHandler exceptionHandler;

    private GenericFutureListener<ChannelFuture> connectedListener;

    private Bootstrap bootstrap;
    private EventLoopGroup worker;

    public void doConnect() {
        try {
            bootstrap = new Bootstrap();
            worker = new NioEventLoopGroup();
            bootstrap.group(worker).channel(NioSocketChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("decoder", new ProtobufDecoder());
                            ch.pipeline().addLast("encoder", new ProtobufEncoder());
                            ch.pipeline().addLast(loginResponseHandler);
                            ch.pipeline().addLast(chatMsgHandler);
                            ch.pipeline().addLast(exceptionHandler);
                        }
                    });
            log.info("客户端开始连接: [joe-im],对端服务端:{}:{}", host, port);

            ChannelFuture future = bootstrap.connect(host,port).sync();
            future.addListener(connectedListener);
        }catch (Exception e){
            log.error("客户端连接错误: {}",e.getMessage());
        }

    }

    public void close(){
        worker.shutdownGracefully();
    }

}

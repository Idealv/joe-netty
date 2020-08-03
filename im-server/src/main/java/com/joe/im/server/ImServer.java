package com.joe.im.server;

import com.joe.im.common.codec.ProtobufDecoder;
import com.joe.im.common.codec.ProtobufEncoder;
import com.joe.im.handler.ChatRedirectHandler;
import com.joe.im.handler.HeartBeatServerHandler;
import com.joe.im.handler.LoginRequestHandler;
import com.joe.im.handler.ServerExceptionHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ImServer {

    @Value("${server.port}")
    private int port;

    @Autowired
    private LoginRequestHandler loginRequestHandler;

    @Autowired
    private ChatRedirectHandler chatRedirectHandler;

    @Autowired
    private ServerExceptionHandler serverExceptionHandler;

    private ServerBootstrap serverBootstrap = new ServerBootstrap();

    private EventLoopGroup boss = new NioEventLoopGroup();

    private EventLoopGroup worker = new NioEventLoopGroup();

    public void run() {
        try {
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .localAddress(port)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("decoder", new ProtobufDecoder());
                            ch.pipeline().addLast("encoder", new ProtobufEncoder());
                            ch.pipeline().addLast(new HeartBeatServerHandler());
                            ch.pipeline().addLast(loginRequestHandler);
                            ch.pipeline().addLast(chatRedirectHandler);
                            ch.pipeline().addLast(serverExceptionHandler);
                        }
                    });
            ChannelFuture future = serverBootstrap.bind().sync();
            log.info("joe-netty server bind to port {} successfully!",port);
            ChannelFuture closeFuture = future.channel().closeFuture();
            closeFuture.sync();
            log.info("joe-netty server channel closed!");
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}

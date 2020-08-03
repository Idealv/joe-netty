package com.joe.im.distributed;

import com.joe.im.common.codec.ProtobufEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Getter
@NoArgsConstructor
@Slf4j
public class WorkerReSender {

    private Channel channel;

    private ImNode remoteNode;

    private volatile boolean isConnected;

    private Bootstrap b;

    private EventLoopGroup g;

    private GenericFutureListener<ChannelFuture> connectedFuture = (ChannelFuture f) -> {
        EventLoop eventLoop = f.channel().eventLoop();
        if (!f.isSuccess()) {
            log.info("连接远端分布式节点失败,3s后尝试重连!");
            eventLoop.schedule(() -> doConnect(), 3, TimeUnit.SECONDS);
            isConnected = false;
        } else {
            isConnected = true;
            channel = f.channel();
            log.info("与远端分布式节点连接成功: {}", remoteNode);
            channel.closeFuture().addListener((ChannelFuture closeFuture) -> {
                log.info("与远端分布式节点连接已断开:{} ", remoteNode);
                isConnected = false;
                channel = null;
                WorkerRouter.getInstance().remove(remoteNode);
                disConnect();
            });
        }
    };


    public WorkerReSender(ImNode remoteNode){
        this.remoteNode = remoteNode;
        b = new io.netty.bootstrap.Bootstrap();
        g = new NioEventLoopGroup();
    }

    public void disConnect(){
        g.shutdownGracefully();
        isConnected = false;
    }

    public void doConnect(){
        String host = remoteNode.getHost();
        int port = Integer.parseInt(remoteNode.getPort());

        if (b!=null&&b.config().group()==null){
            b.group(g).channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .remoteAddress(host,port)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtobufEncoder());
                        }
                    });
            log.info(LocalDateTime.now().toString() + ": 开始连接远端分布式节点:{}", remoteNode);
            ChannelFuture future = b.connect();

            future.addListener(connectedFuture);
        }else if (b.config().group()!=null){
            log.info(LocalDateTime.now().toString() + " 再次尝试连接远端分布式节点:{}", remoteNode);
            b.connect().addListener(connectedFuture);
        }
    }

    public void writeAndFlush(Object pkg){
        if (!isConnected){
            log.error("未建立远端分布式节点的连接:{}", remoteNode);
            return;
        }
        channel.writeAndFlush(pkg);
    }
}

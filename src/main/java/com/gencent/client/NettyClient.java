package com.gencent.client;

import com.gencent.concurrent.CallbackTask;
import com.gencent.handler.*;
import com.gencent.processer.ChatRedirectProcessor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;

@Data
public class NettyClient {

    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;
    private GenericFutureListener<ChannelFuture> connectedListener;

    private ProtoEncoder protoEncoder;
    private LoginResponseHandler loginResponseHandler;

    private HeartBeatClientHandler heartBeatClientHandler;

    private Client client;

    // 服务器ip地址
    private String host;
    // 服务器端口
    private int port;

    public NettyClient(GenericFutureListener<ChannelFuture> connectedListener, Client client) {
        eventLoopGroup = new NioEventLoopGroup();
        this.connectedListener = connectedListener;
        this.client = client;
    }

    // nio线程调用
    public void doConnect() {
        bootstrap = new Bootstrap();
        heartBeatClientHandler = new HeartBeatClientHandler();
        loginResponseHandler = new LoginResponseHandler(client, heartBeatClientHandler);

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .remoteAddress(host, port)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("protoDecoder", new ProtoDecoder());
                        ch.pipeline().addLast("protoEncoder", new ProtoEncoder());
                        ch.pipeline().addLast("loginResponseHandler", loginResponseHandler);
                    }
                });
        bootstrap.connect().addListener(connectedListener);

    }
}

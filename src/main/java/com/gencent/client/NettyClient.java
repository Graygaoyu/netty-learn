package com.gencent.client;

import com.gencent.concurrent.CallbackTask;
import com.gencent.config.Config;
import com.gencent.handler.*;
import com.gencent.processer.ChatRedirectProcessor;
import com.gencent.sender.LoginSender;
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

    private ClientSession session;

    private ProtoEncoder protoEncoder;
    private LoginResponseHandler loginResponseHandler;

    private HeartBeatClientHandler heartBeatClientHandler;

    private Client client;

    private CommandClient commandClient;

    // 服务器ip地址
    private String host;
    // 服务器端口
    private int port;

    private LoginSender loginSender;

    // 初始化
    public NettyClient(ClientSession session, GenericFutureListener<ChannelFuture> connectedListener) {
        this.session = session;
        loginSender = new LoginSender(session);
        eventLoopGroup = new NioEventLoopGroup();
        this.connectedListener = connectedListener;
    }

    public NettyClient(GenericFutureListener<ChannelFuture> connectedListener, Client client) {
        eventLoopGroup = new NioEventLoopGroup();
        this.connectedListener = connectedListener;
        this.client = client;
    }

    // 设置监听器
    public void setConnectedListener(GenericFutureListener<ChannelFuture> connectedListener) {
        this.connectedListener = connectedListener;
    }

    // 连接到服务器
    public void connect() {
        host = Config.HOST;
        port = Config.PORT;
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

    public void login() {
        loginSender.sendLoginMsg();
    }

//    public void execute(ClientState state) {
//        switch (state) {
//            case INIT:
//                this.bind();
//                this.connect();
//            case CONNECTED:
//                this.login();
//        }
//    }
}

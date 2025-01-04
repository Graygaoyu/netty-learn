package com.gencent.server;

import com.gencent.config.Config;
import com.gencent.handler.*;
import com.gencent.processer.ChatRedirectProcessor;
import com.gencent.processer.LoginProcessor;
import com.gencent.server.session.SessionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.util.concurrent.EventExecutorGroup;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Server {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private LoginProcessor loginProcessor;
    private ChatRedirectProcessor chatRedirectProcessor;

    private LoginRequestHandler loginRequestHandler;

    private HeartBeatServerHandler heartBeatServerHandler;

    private ServerBootstrap serverBootstrap = new ServerBootstrap();

    private SessionManager sessionManager = new SessionManager();

    private ChatRedirectHandler chatRedirectHandler;

    private void run() {

        loginProcessor = new LoginProcessor(sessionManager);
        heartBeatServerHandler = new HeartBeatServerHandler(sessionManager);
        loginRequestHandler = new LoginRequestHandler(loginProcessor, heartBeatServerHandler);
        chatRedirectProcessor = new ChatRedirectProcessor(sessionManager);
        chatRedirectHandler = new ChatRedirectHandler(chatRedirectProcessor);

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        String ip = getHostAddress();
        System.out.println("server "+ip);
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(ip, Config.PORT))
//                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("protoDecoder", new ProtoDecoder());
                        ch.pipeline().addLast("protoEncoder",new ProtoEncoder());
                        ch.pipeline().addLast("loginRequestHandler", loginRequestHandler);
                        ch.pipeline().addLast("chatRedirectHandler", chatRedirectHandler);
                    }
                });
        Runtime.getRuntime().addShutdownHook(
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 8 优雅关闭EventLoopGroup，
                        // 释放掉所有资源包括创建的线程
                        workerGroup.shutdownGracefully();
                        bossGroup.shutdownGracefully();
                    }
                }));

        try {
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private static String getHostAddress() {

        String ip = null;
        try
        {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return ip;
    }

    private void init() {

    }

    public static void main(String[] args) {
        Server server = new Server();
        server.init();
        server.run();
    }
}

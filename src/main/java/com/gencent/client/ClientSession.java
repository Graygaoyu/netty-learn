package com.gencent.client;

import com.gencent.pojo.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import static com.gencent.config.Config.SESSION_KEY_NAME;

@Data
public class ClientSession {
    public static final AttributeKey<ClientSession> SESSION_KEY =
            AttributeKey.valueOf(SESSION_KEY_NAME);

    private User user;

    private NettyClient nettyClient;

    private Channel channel;

    private String sessionId = "-1";

    private boolean isConnected = false;

    private boolean isLogin = false;

    private int loginCount = 0;

    private int connectCount = 0;

    private Map<String, Object> map = new HashMap<String, Object>();

    private volatile ClientState state;

    public ClientSession() {
        state = ClientState.INIT;
    }

    // 发起连接到服务器
    public void connectToServer() {
//        nettyClient.bind();
//        nettyClient.setConnectedListener(   );
//        nettyClient.connect();
    }

    // 绑定通道
    public synchronized void bindChannel(Channel channel) {
        this.channel = channel;
        channel.attr(ClientSession.SESSION_KEY).set(this);
    }

    // 发送数据
    public ChannelFuture witeAndFlush(Object pkg)
    {
        return channel.writeAndFlush(pkg);
    }

    // 发起关闭会话
    public synchronized void close()
    {
        System.out.println("closeListener called");
        System.out.println("channel closed");
        channel.attr(ClientSession.SESSION_KEY).set(null);
        channel = null;
        sessionId = "null";
        isConnected = false;
        loginCount = 0;
    }

    // 根据Context获取会话
    public static ClientSession getSession(ChannelHandlerContext ctx)
    {
        Channel channel = ctx.channel();
        return channel.attr(ClientSession.SESSION_KEY).get();
    }



    public synchronized boolean isConnected() {
        return isConnected;
    }

    public synchronized boolean needLogin() {
        return !isLogin && loginCount < 3;
    }

    public synchronized boolean isLogin() {
        return isLogin;
    }

    public synchronized void incrementConnectCount() {
        connectCount++;
    }

    public synchronized void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public synchronized boolean needConnect() {
        return !isConnected && connectCount < 3;
    }





}

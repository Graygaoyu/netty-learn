package com.gencent.client;

import com.gencent.pojo.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.gencent.config.Config.SESSION_KEY_NAME;

@Data
public class ClientSession {
    public static final AttributeKey<ClientSession> SESSION_KEY =
            AttributeKey.valueOf(SESSION_KEY_NAME);

    private User user;

    private Channel channel;

    private String sessionId;

    private boolean isConnected = false;

    private boolean isLogin = false;

    private int loginCount = 0;

    private int connectCount = 0;

    private Map<String, Object> map = new HashMap<String, Object>();

    public ClientSession() {}

    public ClientSession(Channel channel)
    {
        this.channel = channel;
        this.sessionId = "null";
        channel.attr(ClientSession.SESSION_KEY).set(this);
    }

    public ChannelFuture witeAndFlush(Object pkg)
    {
        return channel.writeAndFlush(pkg);
    }

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

    public synchronized void close()
    {
        isConnected = false;

        ChannelFuture future = channel.close();
        future.addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception
            {
                if (future.isSuccess())
                {
                    // TODO
                }
            }
        });
    }

}

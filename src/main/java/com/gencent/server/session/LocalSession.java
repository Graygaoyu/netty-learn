package com.gencent.server.session;

import com.gencent.pojo.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Data;

import java.util.UUID;

@Data
public class LocalSession implements ServerSession {

    public static final AttributeKey<String> KEY_USER_ID =
            AttributeKey.valueOf("key_user_id");

    public static final AttributeKey<LocalSession> SESSION_KEY =
            AttributeKey.valueOf("SESSION_KEY");

    public static final AttributeKey<String> CHANNEL_NAME =
            AttributeKey.valueOf("CHANNEL_NAME");

    private Channel channel;

    private String sessionId;

    private User user;

    private boolean isLogin = false;

    public LocalSession(Channel channel) {
        this.channel = channel;
        this.sessionId = buildNewSessionId();
    }

    public static LocalSession getSession(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        return channel.attr(LocalSession.SESSION_KEY).get();
    }

    private static String buildNewSessionId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public LocalSession bind() {
        System.out.println(" LocalSession 绑定会话 " + channel.remoteAddress());
        channel.attr(LocalSession.SESSION_KEY).set(this);
        channel.attr(LocalSession.CHANNEL_NAME).set(user.getUserId() + "@"+user.getToken());
        isLogin = true;
        return this;
    }

    @Override
    public void writeAndFlush(Object pkg) {
        if (channel.isWritable()) //低水位
        {
            channel.writeAndFlush(pkg);
        } else {   //高水位时
            System.out.println("通道很忙，消息被暂存了");
            //写入消息暂存的分布式存储，如果mongo
            //等channel空闲之后，再写出去
        }
    }

    public synchronized void writeAndClose(Object pkg) {
        channel.writeAndFlush(pkg);
        close();
    }

    public synchronized void close() {
        //用户下线 通知其他节点

        ChannelFuture closeFuture = channel.close();
        closeFuture.addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                System.out.println("CHANNEL_CLOSED error ");
            } else if (future.isSuccess()) {
                System.out.println("CHANNEL_CLOSED success ");
            }
        });
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public String getUserId() {
        return user.getUserId();
    }
}

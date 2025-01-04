package com.gencent.handler;

import com.gencent.concurrent.CallbackTask;
import com.gencent.concurrent.CallbackTaskScheduler;
import com.gencent.pojo.MessageProto;
import com.gencent.server.session.ServerSession;
import com.gencent.server.session.SessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class HeartBeatServerHandler extends IdleStateHandler {

    private static final int READ_IDLE_GAP = 1500;

    private SessionManager sessionManager;

    public HeartBeatServerHandler(SessionManager sessionManager) {
        super(READ_IDLE_GAP, 0, 0, TimeUnit.SECONDS);
        this.sessionManager = sessionManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof MessageProto.Message message))
        {
            super.channelRead(ctx, msg);
            return;
        }
        MessageProto.HeadType type = message.getType();
        if (!type.equals(MessageProto.HeadType.HEARTBEAT_REQUEST)) {
            super.channelRead(ctx, msg);
            return;
        }
        MessageProto.MessageHeartBeat heartBeat = message.getHeartBeat();
        String uid = heartBeat.getUid();
        MessageProto.Message heartBeatMessage = MessageProto.Message.newBuilder()
                .setType(MessageProto.HeadType.HEARTBEAT_RESPONSE)
                .setSessionId(sessionManager.getSession(uid).getSessionId())
                .setSequence(1L)
                .setHeartBeat(heartBeat).build();


        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                if (ctx.channel().isActive())
                {
                    ctx.writeAndFlush(heartBeatMessage);
                }
                return true;
            }

            @Override
            public void onBack(Boolean aBoolean) {

            }

            @Override
            public void onException(Throwable t) {

            }
        });
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        super.channelIdle(ctx, evt);
    }
}

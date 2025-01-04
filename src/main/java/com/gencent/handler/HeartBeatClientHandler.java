package com.gencent.handler;

import com.gencent.client.ClientSession;
import com.gencent.pojo.MessageProto;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {

    private static final int HEARTBEAT_INTERVAL = 50;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ClientSession session = ClientSession.getSession(ctx);
        MessageProto.Message message =
                MessageProto.Message.newBuilder()
                        .setType(MessageProto.HeadType.HEARTBEAT_REQUEST)
                        .setSequence(1)
                        .setSessionId(session.getSessionId())
                        .setHeartBeat(
                                MessageProto.MessageHeartBeat.newBuilder()
                                        .setSeq(1)
                                        .setUid(session.getUser().getUserId()).build())
                        .build();
        System.out.println("heart beat "+message);
        heartBeat(ctx, message);
    }

    private void heartBeat(ChannelHandlerContext ctx,
                           MessageProto.Message heartbeatMsg)
    {
        ctx.executor().schedule(() ->
        {

            if (ctx.channel().isActive())
            {
//                log.info(" 发送 HEART_BEAT  消息 to server");
                ctx.writeAndFlush(heartbeatMsg);

                //递归调用，发送下一次的心跳
                heartBeat(ctx, heartbeatMsg);
            }

        }, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }


}

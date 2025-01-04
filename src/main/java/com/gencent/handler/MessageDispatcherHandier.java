package com.gencent.handler;

import com.gencent.pojo.MessageProto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
public class MessageDispatcherHandier extends ChannelInboundHandlerAdapter {

    private final ConcurrentHashMap<Long, Channel> channelMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        MessageProto.Message message = (MessageProto.Message) msg;
//        MessageProto.Command command = message.getCommand();
//        long userId = message.getUserId();
//        if (command == MessageProto.Command.LOGIN) {
//            channelMap.put(userId, ctx.channel());
//            System.out.println(channelMap);
//        } else if (command == MessageProto.Command.SEND) {
//            for (Map.Entry<Long, Channel> entry : channelMap.entrySet()) {
//                Long uid = entry.getKey();
//                if (uid != userId) {
//                    Channel channel = entry.getValue();
//                    channel.writeAndFlush(message);
//                }
//            }
//            ctx.writeAndFlush(MessageProto.Message.newBuilder().setCommand(MessageProto.Command.ACK)
//                    .setTs(System.currentTimeMillis()).build());
//        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
}

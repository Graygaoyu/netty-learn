package com.gencent.handler;

import com.gencent.pojo.MessageProto;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SendMessageHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        if (msg instanceof MessageProto.Message message) {
//            MessageProto.Command command = message.getCommand();
//            if (command == MessageProto.Command.ACK)
//                System.out.println("have sent");
//            else if (command == MessageProto.Command.SEND)
//                System.out.println(message.getUserId() + " said: " + message.getData());
//        }
//        else
//            super.channelRead(ctx, msg);
    }
}

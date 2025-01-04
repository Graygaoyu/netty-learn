package com.gencent.handler;

import com.gencent.config.Config;
import com.gencent.pojo.MessageProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ProtoEncoder extends MessageToByteEncoder<MessageProto.Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageProto.Message msg, ByteBuf out) throws Exception {
        out.writeShort(Config.MAGIC_CODE);
        out.writeShort(Config.VERSION_CODE);
        byte[] bytes = msg.toByteArray();
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}

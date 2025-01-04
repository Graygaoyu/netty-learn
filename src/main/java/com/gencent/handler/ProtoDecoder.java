package com.gencent.handler;

import com.gencent.pojo.MessageProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ProtoDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        if (in.readableBytes() < 8) {
            return;
        }
        short magic = in.readShort();
        System.out.println(magic);
        short version = in.readShort();
        System.out.println(version);
        int length = in.readInt();
        if (length < 0) {
            return;
        }
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        byte[] array;

        if (in.hasArray()) {
            ByteBuf slice = in.slice(in.readerIndex(), length);
            array = slice.array();
            in.retain();
        } else {
            array = new byte[length];
            in.readBytes(array, 0, length);
        }

        MessageProto.Message message = MessageProto.Message.parseFrom(array);
        System.out.println(message);
        if (in.hasArray())
            in.release();
        if (message != null)
            out.add(message);
    }
}

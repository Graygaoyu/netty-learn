package com.gencent.handler;

import com.gencent.concurrent.CallbackTask;
import com.gencent.concurrent.CallbackTaskScheduler;
import com.gencent.pojo.MessageProto;
import com.gencent.processer.ChatRedirectProcessor;
import com.gencent.server.session.LocalSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class ChatRedirectHandler extends ChannelInboundHandlerAdapter {

    private ChatRedirectProcessor chatRedirectProcessor;

    public ChatRedirectHandler(ChatRedirectProcessor chatRedirectProcessor) {
        this.chatRedirectProcessor = chatRedirectProcessor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof MessageProto.Message message))
        {
            super.channelRead(ctx, msg);
            return;
        }
        MessageProto.HeadType type = message.getType();
        if (!type.equals(MessageProto.HeadType.MESSAGE_REQUEST)) {
            super.channelRead(ctx, msg);
            return;
        }
        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                LocalSession session = LocalSession.getSession(ctx);
                if (null != session && session.isLogin())
                {
                    chatRedirectProcessor.action(session, message);
                    return true;
                }
                return false;
            }

            @Override
            public void onBack(Boolean aBoolean) {

            }

            @Override
            public void onException(Throwable t) {

            }
        });

    }
}

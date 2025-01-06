package com.gencent.handler;

import com.gencent.concurrent.CallbackTask;
import com.gencent.concurrent.CallbackTaskScheduler;
import com.gencent.pojo.MessageProto;
import com.gencent.processer.LoginProcessor;
import com.gencent.server.session.LocalSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class LoginRequestHandler extends ChannelInboundHandlerAdapter {

    private LoginProcessor loginProcesser;

    private HeartBeatServerHandler heartBeatServerHandler;

    public LoginRequestHandler(LoginProcessor loginProcesser, HeartBeatServerHandler heartBeatServerHandler) {
        this.loginProcesser = loginProcesser;
        this.heartBeatServerHandler = heartBeatServerHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof MessageProto.Message message))
        {
            super.channelRead(ctx, msg);
            return;
        }
        MessageProto.HeadType type = message.getType();
        if (!type.equals(MessageProto.HeadType.LOGIN_REQUEST)) {
            super.channelRead(ctx, msg);
            return;
        }

        LocalSession session = new LocalSession(ctx.channel());
        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                return loginProcesser.action(session, message);
            }

            @Override
            public void onBack(Boolean r) {
                if (r) {
                    ctx.pipeline().addAfter("loginRequestHandler", "heartBeatServerHandler", heartBeatServerHandler);
                    ctx.pipeline().remove("loginRequestHandler");
                    System.out.println("login has removed");

                }
            }

            @Override
            public void onException(Throwable t) {
                t.printStackTrace();
            }
        });
    }
}

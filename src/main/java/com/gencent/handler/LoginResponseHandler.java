package com.gencent.handler;

import com.gencent.client.Client;
import com.gencent.concurrent.CallbackTask;
import com.gencent.concurrent.CallbackTaskScheduler;
import com.gencent.pojo.MessageProto;
import com.gencent.client.ClientSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class LoginResponseHandler extends ChannelInboundHandlerAdapter {

    private Client client;

    private HeartBeatClientHandler heartBeatClientHandler;

    public LoginResponseHandler(Client client, HeartBeatClientHandler heartBeatClientHandler) {
        this.client = client;
        this.heartBeatClientHandler = heartBeatClientHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 调用task，这个task应该是client传入的，当失败时执行，更改login状态
        if (!(msg instanceof MessageProto.Message message)) {
            super.channelRead(ctx, msg);
            return;
        }

        MessageProto.HeadType type = message.getType();
        if (!type.equals(MessageProto.HeadType.LOGIN_RESPONSE)) {
            super.channelRead(ctx, msg);
            return;
        }

        MessageProto.LoginResponse loginResponse = message.getLoginResponse();
        int code = loginResponse.getCode();
        if (code != 0) {
            System.out.println(loginResponse.getInfo());
            CallbackTaskScheduler.add(client.getLoginFailedTask());
        } else {
            ClientSession session =
                    ctx.channel().attr(ClientSession.SESSION_KEY).get();
            session.setSessionId(message.getSessionId());
            session.setLogin(true);
            System.out.println(session);
            client.notifyCommandThread();
            ctx.channel().pipeline().addAfter("loginResponseHandler", "heartBeatClientHandler", heartBeatClientHandler);
            heartBeatClientHandler.channelActive(ctx);
            ctx.channel().pipeline().remove("loginResponseHandler");
        }
    }
}

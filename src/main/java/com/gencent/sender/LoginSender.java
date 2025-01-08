package com.gencent.sender;

import com.gencent.client.ClientSession;
import com.gencent.pojo.MessageProto;

public class LoginSender extends BaseSender{

    public LoginSender(ClientSession session) {
        super(session);
    }

    public void sendLoginMsg()
    {
        System.out.println(getSession());
        MessageProto.Message message =
                MessageProto.Message.newBuilder()
                        .setType(MessageProto.HeadType.LOGIN_REQUEST)
                        .setSequence(System.currentTimeMillis())
                        .setSessionId(getSession().getSessionId())
                        .setLoginRequest(
                                MessageProto.LoginRequest.newBuilder()
                                        .setUid(getSession().getUser().getUserId())
                                        .setToken(getSession().getUser().getToken())
                                        .build())
                        .build();
        System.out.println(message);
        super.sendMsg(message);
    }
}

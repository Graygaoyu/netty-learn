package com.gencent.sender;

import com.gencent.pojo.MessageProto;

public class LoginSender extends BaseSender{
    public void sendLoginMsg()
    {
        if (!isConnected())
        {
            return;
        }
        MessageProto.Message message =
                MessageProto.Message.newBuilder()
                        .setType(MessageProto.HeadType.LOGIN_REQUEST)
                        .setSequence(System.currentTimeMillis())
                        .setSessionId(getSession().getSessionId())
                        .setLoginRequest(
                                MessageProto.LoginRequest.newBuilder()
                                        .setUid(getUser().getUserId())
                                        .setToken(getUser().getToken())
                                        .build())
                        .build();
        System.out.println(message);
        super.sendMsg(message);
    }
}

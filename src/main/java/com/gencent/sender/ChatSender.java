package com.gencent.sender;

import com.gencent.pojo.MessageProto;

public class ChatSender extends BaseSender {
    public void sendChatMsg(String toUid, String content) {
        MessageProto.Message message =
                MessageProto.Message.newBuilder()
                        .setType(MessageProto.HeadType.MESSAGE_REQUEST)
                        .setSequence(1)
                        .setSessionId(getSession().getSessionId())
                        .setMessageRequest(
                                MessageProto.MessageRequest.newBuilder()
                                        .setMsgId(1L)
                                        .setFrom(getUser().getUserId())
                                        .setTo(toUid)
                                        .setTime(System.currentTimeMillis())
                                        .setMsgType(1)
                                        .setContent(content).build())
                        .build();
        System.out.println(message);
        super.sendMsg(message);
    }
}

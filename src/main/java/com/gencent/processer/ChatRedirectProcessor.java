package com.gencent.processer;

import com.gencent.pojo.MessageProto;
import com.gencent.server.session.LocalSession;
import com.gencent.server.session.ServerSession;
import com.gencent.server.session.SessionManager;

public class ChatRedirectProcessor extends AbstractProcessor{

    private SessionManager sessionManager;

    public ChatRedirectProcessor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public MessageProto.HeadType op() {
        return null;
    }

    @Override
    public Boolean action(LocalSession ch, MessageProto.Message message) {
        MessageProto.MessageRequest messageRequest = message.getMessageRequest();
        String to = messageRequest.getTo();

        ServerSession toSession = sessionManager.getSession(to);
        System.out.println(messageRequest);
        if (toSession == null) {
            System.out.println(to + " not online");
        } else {
            if (toSession instanceof LocalSession localSession) {
                localSession.writeAndFlush(message);
            }
        }

        return true;
    }
}

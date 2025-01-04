package com.gencent.processer;

import com.gencent.pojo.MessageProto;
import com.gencent.server.session.LocalSession;

public interface ServerReciever {
    MessageProto.HeadType op();

    Boolean action(LocalSession ch, MessageProto.Message proto);
}

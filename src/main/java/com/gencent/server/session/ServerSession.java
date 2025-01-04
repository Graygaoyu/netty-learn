package com.gencent.server.session;

public interface ServerSession {
    void writeAndFlush(Object pkg);

    String getSessionId();

    boolean isValid();

    String getUserId();
}

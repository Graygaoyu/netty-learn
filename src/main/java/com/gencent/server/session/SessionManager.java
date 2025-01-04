package com.gencent.server.session;

import io.netty.util.internal.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private Map<String, ServerSession> sessionMap = new HashMap<>();
    private Map<String, String> userIdMap = new HashMap<>();

    public synchronized void addLocalSession(LocalSession session)
    {
        //step1: 保存本地的session 到会话清单
        String sessionId = session.getSessionId();
        sessionMap.put(sessionId, session);
        userIdMap.put(session.getUserId(), sessionId);
    }

    public synchronized ServerSession getSession(String userId) {
        System.out.println(userIdMap);
        System.out.println(sessionMap);
        String sessionId = userIdMap.get(userId);
        if (sessionId == null)
            return null;
        return sessionMap.get(sessionId);
    }




}

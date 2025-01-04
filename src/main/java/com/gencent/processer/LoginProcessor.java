package com.gencent.processer;

import com.gencent.pojo.MessageProto;
import com.gencent.pojo.User;
import com.gencent.server.session.LocalSession;
import com.gencent.server.session.SessionManager;

public class LoginProcessor extends AbstractProcessor{

    SessionManager sessionManager;

    public LoginProcessor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public MessageProto.HeadType op() {
        return MessageProto.HeadType.LOGIN_REQUEST;
    }

    @Override
    public Boolean action(LocalSession session, MessageProto.Message proto) {
        MessageProto.LoginRequest loginRequest = proto.getLoginRequest();
        long sequence = proto.getSequence();
        String uid = loginRequest.getUid();
        String token = loginRequest.getToken();
        User user = new User();
        user.setUserId(uid);
        user.setToken(token);
        boolean checkResult = checkUser(user);
        if (!checkResult) {
            MessageProto.Message message = MessageProto.Message.newBuilder().setType(MessageProto.HeadType.LOGIN_RESPONSE)
                    .setSequence(sequence).setSessionId("null")
                    .setLoginResponse(MessageProto.LoginResponse.newBuilder()
                            .setResult(false)
                            .setCode(1)
                            .setInfo("登录失败")
                            .setExpose(1).build()).build();
            session.writeAndFlush(message);
            System.out.println(message);
            return false;
        }

        session.setUser(user);
        session.bind();
        sessionManager.addLocalSession(session);

        MessageProto.Message message = MessageProto.Message.newBuilder().setType(MessageProto.HeadType.LOGIN_RESPONSE)
                .setSequence(sequence).setSessionId(session.getSessionId())
                .setLoginResponse(MessageProto.LoginResponse.newBuilder()
                        .setResult(true)
                        .setCode(0)
                        .setInfo("success")
                        .setExpose(1).build()).build();
        session.writeAndFlush(message);
        System.out.println(message);

        return true;
    }

    private boolean checkUser(User user) {

        System.out.println(user);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("check finish");
        //校验用户,比较耗时的操作,需要100 ms以上的时间
        //方法1：调用远程用户restfull 校验服务
        //方法2：调用数据库接口校验
        return user.getUserId().contains("root") && user.getToken().equals("123");

//        List<ServerSession> l = sessionManger.getSessionsBy(user.getUserId());
//
//
//        if (null != l && l.size() > 0)
//        {
//            return false;
//        }
    }
}

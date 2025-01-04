package com.gencent.client;

import com.gencent.command.*;
import com.gencent.concurrent.CallbackTask;
import com.gencent.config.Config;
import com.gencent.pojo.User;
import com.gencent.sender.ChatSender;
import com.gencent.sender.LoginSender;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Client {

    private Scanner scanner;
    private NettyClient nettyClient;
    private Map<String, BaseCommand>  commandMap = new HashMap<>();

    private Channel channel;

    private ClientSession session;

    private LoginSender loginSender;

    private ChatSender chatSender;

    private User user;

    private CallbackTask<Void> loginFailedTask = new CallbackTask<Void>() {
        @Override
        public Void execute() throws Exception {
            loginFailedPostProcess();
            loginFlag = false;
            return null;
        }

        @Override
        public void onBack(Void unused) {

        }

        @Override
        public void onException(Throwable t) {

        }
    };

    private GenericFutureListener<ChannelFuture> closeListener = new GenericFutureListener<ChannelFuture>() {
        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
//            channel = channelFuture.channel();
//            ClientSession session =
//                    channel.attr(ClientSession.SESSION_KEY).get();
//            session.close();
//            connectFlag = false;
//            setLoginTime(0);
//            System.out.println("closeListener called");
//            System.out.println("channel closed");
//            //唤醒用户线程
//            notifyCommandThread();
        }
    };

    private synchronized void setLoginTime(int loginTime) {
        this.loginTime.set(loginTime);
    }

    // nio线程调用
    private GenericFutureListener<ChannelFuture> connectedListener = new GenericFutureListener<ChannelFuture>() {
        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
            System.out.println("connectedListener operationComplete");
            EventLoop eventLoop = channelFuture.channel().eventLoop();
            if (!channelFuture.isSuccess() && session.needConnect()) {
                eventLoop.schedule(() -> nettyClient.doConnect(),
                        10,
                        TimeUnit.SECONDS);
                session.incrementConnectCount();
                session.setIsConnected(false);
            } else if (channelFuture.isSuccess()) {
                connectFlag = true;
                channel = channelFuture.channel();
                channel.closeFuture().addListener(closeListener);

                session = new ClientSession(channel);
                session.setConnected(true);

                notifyCommandThread();
            } else {
                connectFlag = false;
                //唤醒用户线程
                notifyCommandThread();
            }
        }
    };

    public synchronized void waitCommandThread()
    {

        //休眠，命令收集线程
        try
        {
            this.wait();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }

    public synchronized void notifyCommandThread()
    {
        this.notify();
    }

    public synchronized void loginFailedPostProcess() {
        loginTime.incrementAndGet();
        this.notify();
    }

    private void init() {
        scanner = new Scanner(System.in);
        Thread.currentThread().setName("主线程");
        initCommandMap();
        initNettyClient();
        session = new ClientSession();
    }

    private void initCommandMap() {
        BaseCommand clientCommandMenu = new ClientCommandMenu();
        BaseCommand chatConsoleCommand = new ChatConsoleCommand();
        BaseCommand loginConsoleCommand = new LoginConsoleCommand();
        BaseCommand logoutConsoleCommand = new LogoutConsoleCommand();
        commandMap.put(clientCommandMenu.getKey(), clientCommandMenu);
        commandMap.put(chatConsoleCommand.getKey(), chatConsoleCommand);
        commandMap.put(loginConsoleCommand.getKey(), loginConsoleCommand);
        commandMap.put(logoutConsoleCommand.getKey(), logoutConsoleCommand);
    }

    private void initNettyClient() {
        nettyClient = new NettyClient(connectedListener, this);
    }

    private void run() {
        System.out.println("start to run");
        while (true) {
            if (!session.isConnected()) {
                System.out.println("start to connect");
                connectToServer();
            }
            if (session.needLogin()) {
                userLogin();
            } else {
                System.out.println(" channel close do");
                channel.close();
            }
            while (session.isLogin())
            {
                ChatConsoleCommand command = (ChatConsoleCommand) commandMap.get(ChatConsoleCommand.KEY);
                command.exec(scanner);
                startOneChat(command);
            }
        }
    }

    private void startOneChat(ChatConsoleCommand c)
    {
        //登录
        if (!session.isLogin())
        {
            return;
        }
        chatSender = new ChatSender();
        chatSender.setSession(session);
        chatSender.setUser(user);
        chatSender.sendChatMsg(c.getToUserId(), c.getMessage());

    }

    private void userLogin() {
        LoginConsoleCommand loginConsoleCommand = (LoginConsoleCommand) commandMap.get(LoginConsoleCommand.KEY);
        loginConsoleCommand.exec(scanner);
        User user = new User();
        user.setUserId(loginConsoleCommand.getUserName());
        user.setToken(loginConsoleCommand.getPassword());
        System.out.println("user " + user);

        System.out.println("start to login");
        this.user = user;
        session.setUser(user);
        loginSender = new LoginSender();
        loginSender.setUser(user);
        loginSender.setSession(session);
        loginSender.sendLoginMsg();
        waitCommandThread();
    }

    private void connectToServer() {
        nettyClient.setHost(Config.HOST);
        nettyClient.setPort(Config.PORT);
        nettyClient.doConnect();
        waitCommandThread();
    }

    public static void main(String[] args) {
        Client application = new Client();
        application.init();
        application.run();
    }
}

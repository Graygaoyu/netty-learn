package com.gencent.client;

import com.gencent.command.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CommandClient implements Runnable {

    private Map<String, BaseCommand> commandMap = new HashMap<>();

    private Map<ClientState, List<BaseCommand>> validStateActionMap;

    private Scanner scanner;

    BaseCommand loginConsoleCommand;

    private BaseCommand currentCommand;

    private volatile ClientSession session;

    private NettyClient nettyClient;

    private GenericFutureListener<ChannelFuture> connectedListener = new GenericFutureListener<ChannelFuture>() {
        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
            System.out.println("connectedListener operationComplete");
            EventLoop eventLoop = channelFuture.channel().eventLoop();
            if (!channelFuture.isSuccess() && session.needConnect()) {
//                eventLoop.schedule(() -> nettyClient.connect(),
//                        10,
//                        TimeUnit.SECONDS);
//                session.incrementConnectCount();
//                session.setIsConnected(false);
//                System.out.println("connect failed num "+session.getConnectCount());
            } else if (channelFuture.isSuccess()) {
                session.setIsConnected(true);
                session.setState(ClientState.CONNECTED);
                Channel channel = channelFuture.channel();
//                channel.closeFuture().addListener(closeListener);
                session.setChannel(channel);
                System.out.println("connect success");
                notifyCommandThread();
            } else {
//                session.setIsConnected(false);
//                System.out.println(" Max connect time!");
//                //唤醒用户线程
////                notifyCommandThread();
//                System.exit(-1);
            }
        }
    };

    public CommandClient(ClientSession session) {
        this.session = session;
        this.nettyClient =  new NettyClient(session, connectedListener);
        scanner = new Scanner(System.in);
        BaseCommand clientCommandMenu = new ClientCommandMenu();
        BaseCommand chatConsoleCommand = new ChatConsoleCommand();
        loginConsoleCommand = new LoginConsoleCommand();
        BaseCommand logoutConsoleCommand = new LogoutConsoleCommand();
        validStateActionMap = new HashMap<>();
        validStateActionMap.put(ClientState.INIT, new ArrayList<>());
        validStateActionMap.put(ClientState.CONNECTING, new ArrayList<>());
        validStateActionMap.put(ClientState.CONNECTED, List.of(loginConsoleCommand));

//        commandMap.put(clientCommandMenu.getKey(), clientCommandMenu);
//        commandMap.put(chatConsoleCommand.getKey(), chatConsoleCommand);
//        commandMap.put(loginConsoleCommand.getKey(), loginConsoleCommand);
//        commandMap.put(logoutConsoleCommand.getKey(), logoutConsoleCommand);
    }

    @Override
    public void run() {
        while (true) {
            ClientState currentState = session.getState();
            System.out.println(currentState);
            switch (currentState) {
                case INIT:
                    nettyClient.connect();
                    waitCommandThread();
                    break;
                case CONNECTED:
                    loginConsoleCommand.exec(scanner, session);
                    nettyClient.login();
                    waitCommandThread();
            }

        }
    }

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

    public void start() {
        new Thread(this, "command 线程").start();
    }
}

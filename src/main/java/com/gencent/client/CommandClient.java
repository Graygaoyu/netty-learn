package com.gencent.client;

import com.gencent.command.*;

import java.util.*;

public class CommandClient implements Runnable {

    private Map<String, BaseCommand> commandMap = new HashMap<>();

    private Map<ClientState, List<BaseCommand>> validStateActionMap;

    private Scanner scanner;

    private BaseCommand currentCommand;

    private ClientSession session;

    public CommandClient(ClientSession session) {
        this.session = session;
        scanner = new Scanner(System.in);
        BaseCommand clientCommandMenu = new ClientCommandMenu();
        BaseCommand chatConsoleCommand = new ChatConsoleCommand();
        BaseCommand loginConsoleCommand = new LoginConsoleCommand();
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
            List<BaseCommand> commandList = validStateActionMap.get(currentState);
            
        }
    }

    public void start() {
        new Thread(this, "command 线程").start();
    }
}

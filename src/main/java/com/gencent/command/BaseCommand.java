package com.gencent.command;

import com.gencent.client.Client;
import com.gencent.client.ClientSession;

import java.util.Scanner;

public interface BaseCommand {
    void exec(Scanner scanner, ClientSession session);

    String getKey();

    String getTip();
}

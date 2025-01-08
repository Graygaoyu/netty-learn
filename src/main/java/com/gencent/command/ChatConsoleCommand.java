package com.gencent.command;

import com.gencent.client.Client;
import com.gencent.client.ClientSession;
import lombok.Data;

import java.util.Scanner;

@Data
public class ChatConsoleCommand implements BaseCommand {

    private String toUserId;
    private String message;
    public static final String KEY = "2";

    @Override
    public void exec(Scanner scanner, ClientSession session) {
        System.out.println("请输入聊天信息，格式为：内容@用户名 ");

        while (true) {

            try {

                String s = scanner.next();
                String[] array = s.split("@");

                message = array[0];
                toUserId = array[1];

                break;
            } catch (Throwable t) {
                System.out.println(" 深入有误 ,请输入聊天信息，格式为：内容@用户名 ");

            }

        }
    }


    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getTip() {
        return "聊天";
    }

}

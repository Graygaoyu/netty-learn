package com.gencent.command;

import com.gencent.client.ClientSession;

import java.util.Scanner;

public class LogoutConsoleCommand implements BaseCommand
{
    public static final String KEY = "10";

    @Override
    public void exec(Scanner scanner, ClientSession session)
    {

    }


    @Override
    public String getKey()
    {
        return KEY;
    }

    @Override
    public String getTip()
    {
        return "退出";
    }

}

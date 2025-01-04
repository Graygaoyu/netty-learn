package com.gencent.command;

import lombok.Data;

import java.util.Scanner;

@Data
public class LoginConsoleCommand implements BaseCommand
{
    public static final String KEY = "1";

    private String userName;
    private String password;

    @Override
    public void exec(Scanner scanner)
    {
        System.out.println("请输入登录信息，格式为：用户名@密码 ");
        String s = scanner.next();
        String[] array = s.split("@");

        userName = array[0];
        password = array[1];
    }

    @Override
    public String getKey()
    {
        return KEY;
    }

    @Override
    public String getTip()
    {
        return "登录";
    }

}

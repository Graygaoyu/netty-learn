package com.gencent.command;

import com.gencent.client.ClientSession;
import com.gencent.pojo.User;
import lombok.Data;

import java.util.Scanner;

@Data
public class LoginConsoleCommand implements BaseCommand
{
    public static final String KEY = "1";

    @Override
    public void exec(Scanner scanner, final ClientSession session)
    {
        System.out.println("请输入登录信息，格式为：用户名@密码 ");
        String s = scanner.next();
        String[] array = s.split("@");
        session.setUser(new User(array[0], array[1]));
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

package com.joe.im.command;

import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.security.auth.login.Configuration;
import java.util.Scanner;

@Service
@Getter
public class LogoutConsoleCommand implements BaseCommand {
    public static final String KEY = "10";

     public enum Confirm{
        Y,N;
    }

    private String enter;

    @Override
    public void exec(Scanner scanner) {
        //todo add logout
        System.out.println("确定退出登录吗?Y/N");
        enter = scanner.next();
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getTip() {
        return "退出";
    }

}

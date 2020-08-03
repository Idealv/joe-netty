package com.joe.im.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
@Getter@Setter
public class ChatConsoleCommand implements BaseCommand {

    public static final String KEY = "2";

    private String toUserId;

    private String message;

    @Override
    public void exec(Scanner scanner) {
        System.out.println("请输入聊天的消息:{id:message}");
        String[] info=null;
        while (true){
            info=scanner.next().split(":");
            if (info.length!=2){
                System.out.println("请输入聊天的消息:{id:message}");
            }else {
                break;
            }
        }
        toUserId = info[0];
        message = info[1];
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

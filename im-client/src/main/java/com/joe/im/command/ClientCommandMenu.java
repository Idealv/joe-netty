package com.joe.im.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Getter@Setter
@Service
public class ClientCommandMenu implements BaseCommand{

    public static final String KEY = "0";

    private String allCommandsShow;

    private String commandInput;

    @Override
    public void exec(Scanner scanner) {
        System.out.println("请输入某个操作指令");
        System.out.println(allCommandsShow);

        commandInput = scanner.next();
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getTip() {
        return "show 所有命令";
    }
}

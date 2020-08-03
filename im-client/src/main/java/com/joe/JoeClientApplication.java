package com.joe;

import com.joe.im.client.CommandController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class JoeClientApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(JoeClientApplication.class);
        CommandController commandController = ctx.getBean(CommandController.class);
        commandController.initCommandMap();
        try {
            commandController.startCommandThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

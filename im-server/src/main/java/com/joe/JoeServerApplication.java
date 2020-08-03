package com.joe;

import com.joe.im.server.ImServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class JoeServerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(JoeServerApplication.class);
        ImServer imServer = ctx.getBean(ImServer.class);
        imServer.run();
    }
}

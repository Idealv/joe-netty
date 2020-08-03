package com.joe.im.client;

import com.joe.im.command.*;
import com.joe.im.common.bean.User;
import com.joe.im.concurrent.FutureTaskScheduler;
import com.joe.im.sender.ChatSender;
import com.joe.im.sender.LoginSender;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CommandController {

    @Autowired
    private LoginConsoleCommand loginConsoleCommand;

    @Autowired
    private ClientCommandMenu clientCommandMenu;

    @Autowired
    private LogoutConsoleCommand logoutConsoleCommand;

    @Autowired
    private ChatConsoleCommand chatConsoleCommand;

    @Autowired
    private ImClient imClient;

    @Autowired
    private LoginSender loginSender;

    @Autowired
    private ChatSender chatSender;

    private ClientSession session;

    private Channel channel;

    private User user;

    private boolean isConnected = false;

    private Map<String, BaseCommand> commandMap;

    public void setIsConnected(boolean b){
        this.isConnected=b;
    }

    private GenericFutureListener<ChannelFuture> closeListener = (ChannelFuture f) -> {
        log.info("{} 连接已断开", LocalDateTime.now().toString());
        ClientSession clientSession = f.channel().attr(ClientSession.SESSION_KEY).get();
        clientSession.close();

        notifyCommandThread();
    };

    private GenericFutureListener<ChannelFuture> connectedListener=(ChannelFuture f)->{
        final EventLoop eventLoop = f.channel().eventLoop();
        if (!f.isSuccess()) {
            log.info("登录失败,3秒后尝试重连!");
            f.cause().printStackTrace();
            eventLoop.schedule(
                    () -> imClient.doConnect(),
                    3000,
                    TimeUnit.MILLISECONDS
            );
            isConnected = false;
        }else {
            isConnected = true;
            log.info("joe-im连接到服务器成功!");
            channel = f.channel();

            session = new ClientSession(channel);
            session.setConnected(isConnected);
            notifyCommandThread();

            channel.closeFuture().addListener(closeListener);
        }
    };

    public void initCommandMap(){
        commandMap = new HashMap<>();
        commandMap.put(clientCommandMenu.getKey(), clientCommandMenu);
        commandMap.put(loginConsoleCommand.getKey(), loginConsoleCommand);
        commandMap.put(chatConsoleCommand.getKey(), chatConsoleCommand);
        commandMap.put(logoutConsoleCommand.getKey(), logoutConsoleCommand);

        Set<Map.Entry<String, BaseCommand>> entrySet = commandMap.entrySet();
        Iterator<Map.Entry<String, BaseCommand>> it = entrySet.iterator();
        StringBuilder sb = new StringBuilder();
        sb.append("[menu] ");
        while (it.hasNext()){
            BaseCommand baseCommand = it.next().getValue();
            sb.append(baseCommand.getKey())
                    .append("->")
                    .append(baseCommand.getTip())
                    .append(" | ");
        }

        clientCommandMenu.setAllCommandsShow(sb.toString());
    }

    public void startConnectServer(){
        FutureTaskScheduler.add(()->{
            imClient.setConnectedListener(connectedListener);
            imClient.doConnect();
        });
    }

    private synchronized void notifyCommandThread(){
        this.notify();
    }

    private synchronized void waitCommandThread(){
        try {
            this.wait();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    public void startCommandThread() throws InterruptedException{
        Thread.currentThread().setName("命令行命令收集线程");

        while (true){
            while (!isConnected){
                startConnectServer();
                waitCommandThread();
            }

            while (null!=session){
                Scanner scanner = new Scanner(System.in);
                clientCommandMenu.exec(scanner);
                String key = clientCommandMenu.getCommandInput();
                BaseCommand command = commandMap.get(key);
                if (null==command){
                    System.err.println("无法识别指令 [" + key + "] ,请重新输入!");
                    continue;
                }

                switch (key){
                    case ClientCommandMenu.KEY:
                        System.out.println(clientCommandMenu.getAllCommandsShow());
                        break;
                    case LoginConsoleCommand.KEY:
                        loginConsoleCommand.exec(scanner);
                        startLogin(loginConsoleCommand);
                        break;
                    case LogoutConsoleCommand.KEY:
                        logoutConsoleCommand.exec(scanner);
                        startLogout(logoutConsoleCommand);
                        break;
                    case ChatConsoleCommand.KEY:
                        chatConsoleCommand.exec(scanner);
                        startOneChat(chatConsoleCommand);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void startLogin(LoginConsoleCommand loginConsoleCommand){
        if (!isConnected) {
            log.error("未建立连接,请重新连接!");
            return;
        }
        User user = new User();
        user.setUid(loginConsoleCommand.getUsername());
        user.setToken(loginConsoleCommand.getPassword());
        user.setDevId("1111");
        this.user = user;
        session.setUser(user);
        loginSender.setSession(session);
        loginSender.setUser(user);
        loginSender.sendLoginMsg();
    }

    public void startLogout(LogoutConsoleCommand logoutConsoleCommand) {
        String enter = logoutConsoleCommand.getEnter();
        if (enter!= "Y" || enter != "N") {
            System.out.println("请输入正确指令");
            return;
        }
        if (LogoutConsoleCommand.Confirm.valueOf(enter).equals(LogoutConsoleCommand.Confirm.Y)){
            session.close();
        }else {
            System.out.println("取消退出登录操作");
            return;
        }
    }

    public void startOneChat(ChatConsoleCommand c){
        if (!isLogin()){
            System.err.println("未登录,请先登录");
            return;
        }
        chatSender.setSession(session);
        chatSender.setUser(user);
        chatSender.sendMsg(c.getToUserId(), c.getMessage());
    }

    public boolean isLogin(){
        if (null==session){
            log.info("session is null");
            return false;
        }
        return session.isLogin();
    }
}

package com.joe.im.server;

import com.joe.im.common.bean.User;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class SessionMap {
    private Map<String, ServerSession> map = new ConcurrentHashMap<>();

    private static enum Singleton{
        INSTANCE;

        private SessionMap sessionMap;

        private Singleton(){
            sessionMap = new SessionMap();
        }

        public SessionMap getInstance(){
            return sessionMap;
        }
    }

    public static SessionMap getInstance(){
        return Singleton.INSTANCE.getInstance();
    }

    public void addSession(String sesionId,ServerSession session){
        map.put(sesionId, session);
        log.info("用户登录:id={},在线总数:{}", session.getUser().getUid(), map.size());
    }

    public ServerSession getSession(String sessionId){
        if (map.containsKey(sessionId)){
            return map.get(sessionId);
        }else {
            return null;
        }
    }

    public void removeSession(String sessionId){
        if (!map.containsKey(sessionId)){
            return;
        }
        ServerSession session = map.get(sessionId);
        map.remove(sessionId);
        log.info("用户下线:id={},在线总数:{}", session.getUser().getUid(), map.size());
    }

    public boolean hasLogin(User user){
        Iterator<Map.Entry<String, ServerSession>> it = map.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, ServerSession> next = it.next();
            User u = next.getValue().getUser();
            if (u.getUid().equals(user.getUid())&&u.getPlatform().equals(user.getPlatform())){
                return true;
            }
        }
        return false;
    }

    public List<ServerSession> getSessionByUid(String uid) {
        //todo 使用redis改善性能
        return map.values()
                .stream()
                .filter(s -> s.getUser().getUid().equals(uid))
                .collect(Collectors.toList());
    }
}

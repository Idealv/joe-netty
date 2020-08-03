package com.joe.im.common.bean;

import com.joe.im.common.bean.msg.ProtoMsg;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Data
@Slf4j
public class User {
    private String uid;
    private String devId;
    private String token;
    private String nickName = "nickName";
    private String sessionId;
    private PLATTYPE platform = PLATTYPE.WINDOWS;

    public enum PLATTYPE {
        WINDOWS, MAC, ANDROID, IOS, WEB, OTHER;
    }

    public void setPlatform(int platform){
        PLATTYPE[] values = PLATTYPE.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].ordinal()==platform){
                this.platform = values[platform];
            }
        }
    }

    public static User fromMsg(ProtoMsg.LoginRequest info){
        User user = new User();
        user.setUid(info.getUid());
        user.setDevId(info.getDeviceId());
        user.setToken(info.getToken());
        user.setPlatform(info.getPlatform());
        log.info("用户正在登录: {}", user);
        return user;
    }
}

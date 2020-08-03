package com.joe.im.common.bean;

import lombok.*;

import java.io.Serializable;

@ToString
@Getter
@Setter
public class Demo implements Serializable {

    private static final long serialVersionUID = -2410466667020479232L;

    private long id;

    private String content;
}

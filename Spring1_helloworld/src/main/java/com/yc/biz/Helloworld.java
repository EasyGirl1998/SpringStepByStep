package com.yc.biz;

import org.springframework.stereotype.Component;

@Component
public class Helloworld {
    public Helloworld() {
        System.out.println("无参数的构造方法.");
    }

    public void Hello(){
        System.out.println("你好");
    }
}

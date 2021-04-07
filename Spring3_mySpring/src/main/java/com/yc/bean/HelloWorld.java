package com.yc.bean;

import com.yc.springframework.annotation.MyComponent;
import com.yc.springframework.annotation.MyPostConstruct;
import com.yc.springframework.annotation.MyPreDestroy;

import javax.swing.plaf.synth.SynthOptionPaneUI;

@MyComponent
public class HelloWorld {
    @MyPostConstruct
    public void setup(){
        System.out.println("This is MyPostConstruct");
    }

    @MyPreDestroy
    public void destroy(){
        System.out.println("MyPreDestroy");
    }

    public HelloWorld(){
        System.out.println("无参数的构造方法...");
    }

    public void show(){
        System.out.println("show");
    }
}

package com.yc;

import com.yc.biz.Helloworld;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.applet.AppletContext;

@Configuration  //表示当前是一个配置类
@ComponentScan(basePackages = "com.yc")
public class App {
    public static void main(String[] args) {
        //创建ioc容器
        ApplicationContext applicationContext=new AnnotationConfigApplicationContext(App.class);
        //ioc容器自动装配类
        Helloworld helloworld= (Helloworld) applicationContext.getBean("helloworld");
        helloworld.Hello();
    }


}

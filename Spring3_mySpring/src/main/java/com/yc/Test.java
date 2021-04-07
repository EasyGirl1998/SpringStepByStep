package com.yc;

import com.yc.bean.HelloWorld;
import com.yc.biz.StudentBizimpl;
import com.yc.springframework.context.MyAnnotationConfigApplicationContext;
import com.yc.springframework.context.MyApplicationContext;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Test {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException, InvocationTargetException, IOException, ClassNotFoundException {
        MyApplicationContext myApplicationContext=new MyAnnotationConfigApplicationContext(MyAppConfig.class);
//        HelloWorld hw= (HelloWorld) myApplicationContext.getBean("hw");
//        hw.show();
        StudentBizimpl sb= (StudentBizimpl) myApplicationContext.getBean("studentBizimpl");
        sb.add("王晨");
    }
}

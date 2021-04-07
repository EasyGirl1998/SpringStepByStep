package com.yc;

import com.yc.bean.HelloWorld;
import com.yc.springframework.annotation.MyComponentScan;
import com.yc.springframework.annotation.MyConfiguration;
import com.yc.springframework.annotation.NyBean;

@MyConfiguration
@MyComponentScan(basePackages = {"com.yc.biz","com.yc.dao"})
public class MyAppConfig {
//    @NyBean
//    public HelloWorld hw(){
//        return new HelloWorld();
//    }
}

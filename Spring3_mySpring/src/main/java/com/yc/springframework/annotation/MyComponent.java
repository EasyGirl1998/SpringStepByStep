package com.yc.springframework.annotation;


import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)//在什么时候使用注解  runtime指在运行时使用
@Documented
public @interface MyComponent {
}

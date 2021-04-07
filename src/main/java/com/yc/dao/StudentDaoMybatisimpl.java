package com.yc.dao;

import java.util.Random;

public class StudentDaoMybatisimpl implements StudentDao{
    @Override
    public int add(String name) {
        System.out.println("Mybatis添加学生："+name);
        Random r=new Random();
        return r.nextInt();
    }

    @Override
    public void update() {
        System.out.println("Mybatis更新学生");
    }
}

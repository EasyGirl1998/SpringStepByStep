package com.yc.dao;

import com.yc.biz.StudentBizimpl;
import junit.framework.TestCase;

public class StudentDaoTest extends TestCase {

    private StudentDao studentDao;
    private StudentBizimpl studentBizimpl;

    public void setUp() throws Exception {
        /*
        痛点
        1.能否完成自动化实例对象 --》 IOC 控制反转
        2.能否完成自动化装配对象 --> DI 依赖注入
         */

        //痛点1
        studentDao=new StudentDaoJpaImpl();

        //痛点2
        //第一种解决方法，在业务层中创建一个带参数的构造方法
        //studentBizimpl=new StudentBizimpl(studentDao);
        //第二种解决方法：在业务层中创建一个set方法
        studentBizimpl=new StudentBizimpl();
        studentBizimpl.setStudentDao(studentDao);
    }

    public void testAdd() {
        studentDao.add("王晨");
    }

    public void testUpdate() {
        studentDao.update();
    }

    public void testBizadd() {
        studentBizimpl.add("王晨");
    }

}
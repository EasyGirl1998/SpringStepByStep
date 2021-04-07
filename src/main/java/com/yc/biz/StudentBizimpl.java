package com.yc.biz;

import com.yc.dao.StudentDao;
import org.omg.Messaging.SyncScopeHelper;

public class StudentBizimpl {
    public StudentBizimpl(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    public void setStudentDao(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    public StudentBizimpl() {
    }

    private StudentDao studentDao;

    public int add(String name){
        System.out.println("+++++++++++++业务层开始++++++++++++++++++++");
        int result=studentDao.add("王晨");
        System.out.println("+++++++++++++业务层结束++++++++++++++++++++");
        return result;
    }

    public void update(){
        System.out.println("+++++++++++++业务层开始++++++++++++++++++++");
        studentDao.update();
        System.out.println("+++++++++++++业务层结束++++++++++++++++++++");
    }

}

package com.yc.springframework.context;

import com.yc.springframework.annotation.*;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class MyAnnotationConfigApplicationContext implements MyApplicationContext{

    private Map<String,Object> beanMap=new HashMap<>();

    public MyAnnotationConfigApplicationContext(Class<?>... componentClasses) throws InstantiationException, IllegalAccessException, InvocationTargetException, IOException, ClassNotFoundException {
        register(componentClasses);
    }

    private void register(Class<?>[] componentClasses) throws IllegalAccessException, InstantiationException, InvocationTargetException, IOException, ClassNotFoundException {
        //源码1.只实现Ioc
        if( componentClasses==null && componentClasses.length<=0){
            throw new RuntimeException("没有指定配置类");
        }
        for (Class c:componentClasses){
            if(!c.isAnnotationPresent(MyConfiguration.class)){
                continue;
            }
            String[]basePackages=getAppConfigBasePackages(c);
            if(c.isAnnotationPresent(MyComponentScan.class)){
                MyComponentScan mcs= (MyComponentScan) c.getAnnotation(MyComponentScan.class);
                if(mcs.basePackages()!=null && mcs.basePackages().length>0){
                    basePackages=mcs.basePackages();
                }
            }
            //处理@Mybean
            Object obj=c.newInstance();
            handleAtMyBean(c,obj);

            //处理 basePackAges 基础包下的所有托管bean
            for(String basepackage:basePackages){
                scanPackageAndSubPackageClasses(basepackage);
            }
            //继续处理其他bean
            handleMangedBean();

        }


        //版本2.实现di 循环beanMap中的每个bean，找到他们每个类中由@Auto 和#Resource注解的方法实现di
        handleDi(beanMap);
    }

    private void handleDi(Map<String, Object> beanMap) throws InvocationTargetException, IllegalAccessException {
        Collection<Object> objectCollection = beanMap.values();
        for (Object obj : objectCollection) {
            Class cls = obj.getClass();
            Method[] ms = cls.getDeclaredMethods();
            for (Method m : ms) {
                if (m.isAnnotationPresent(MyAutowired.class) && m.getName().startsWith("set")) {
                    invokeAutowiredMethod(m, obj);
                } else if (m.isAnnotationPresent(MyResource.class) && m.getName().startsWith("set")) {
                    invokeResourceMethod(m, obj);
                }
            }
            Field[] fs = cls.getDeclaredFields();
            for (Field field : fs) {
                if (field.isAnnotationPresent(MyAutowired.class)) {

                } else if (field.isAnnotationPresent(MyResource.class)) {

                }
            }
        }

    }

    private void invokeResourceMethod(Method m, Object obj) throws InvocationTargetException, IllegalAccessException {
        //1. 取出  MyResource中的name属性值 ,当成   beanId
        MyResource mr = m.getAnnotation(MyResource.class);
        String beanId = mr.name();
        //2. 如果没有，则取出  m方法中参数的类型名, 改成首字小写   当成beanId
        if (beanId == null || beanId.equalsIgnoreCase("")) {
            String pname = m.getParameterTypes()[0].getSimpleName();
            beanId = pname.substring(0, 1).toLowerCase() + pname.substring(1);
        }
        //3. 从beanMap取出
        Object o = beanMap.get(beanId);
        //4. invoke
        m.invoke(obj, o);
    }

    private void invokeAutowiredMethod(Method m, Object obj) throws InvocationTargetException, IllegalAccessException {
        //1. 取出  m的参数的类型
        Class typeClass = m.getParameterTypes()[0];
        //2. 从beanMap中循环所有的object,
        Set<String> keys = beanMap.keySet();
        for (String key : keys) {
            // 4.  如果是，则从beanMap取出.
            Object o = beanMap.get(key);
            //3. 判断 这些object 是否为   参数类型的实例  instanceof
            Class[] interfaces=o.getClass().getInterfaces();
           for (Class c:interfaces){
               System.out.println(c.getName()+"\t"+typeClass);
               if(c==typeClass){
                   //invoke
                   m.invoke(obj,o);
                   break;
               }
           }
        }
    }

    private void handleMangedBean() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        for(Class c:managedBeanClasses){
            if(c.isAnnotationPresent(MyComponent.class)){
                saveManagedBean(c);
            }else if(c.isAnnotationPresent(MyService.class)){
                saveManagedBean(c);
            }else if(c.isAnnotationPresent(MyRepository.class)){
                saveManagedBean(c);
            }else if(c.isAnnotationPresent(MyController.class)){
                saveManagedBean(c);
            }else {
                continue;
            }
        }
    }

    private void saveManagedBean(Class c) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object o=c.newInstance();
        handlePostConstruct(o,c);
        String beanId=c.getSimpleName().substring(0,1).toLowerCase()+c.getSimpleName().substring(1);
        beanMap.put(beanId,o);
    }

    //扫描包路径和其子包路径
    private void scanPackageAndSubPackageClasses(String basepackage) throws IOException, ClassNotFoundException {
        String path= basepackage.replaceAll("\\.","/");
        System.out.println("替换后的路径："+path);
        Enumeration<URL> files=Thread.currentThread().getContextClassLoader().getResources(path);
        while (files.hasMoreElements()){
            URL url=files.nextElement();
            System.out.println("配置的扫描路径为："+url);
            //TODO:递归这些目录查找.class
            findClassesInPackages(url.getFile(),basepackage);
        }
    }
    private Set<Class> managedBeanClasses =new HashSet<Class>();
    /**
     * 查找file 下面及子包所有的要托管的class，存到一个set中
     * @param file
     * @param basepackage
     */
    private void findClassesInPackages(String file, String basepackage) throws ClassNotFoundException {
        File f=new File(file);
        File[] classFiles=f.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".class") || pathname.isDirectory();
            }
        });
        for (File cf:classFiles){
            if (cf.isDirectory()){
                basepackage+="."+cf.getName().substring(cf.getName().lastIndexOf("/")+1);
                findClassesInPackages( cf.getAbsolutePath() ,basepackage );
            }else {
                URL[] urls=new URL[]{};
                URLClassLoader ucl=new URLClassLoader(urls);
                Class c=ucl.loadClass(basepackage+"."+cf.getName().replace(".class",""));
                managedBeanClasses.add(c  );
            }
        }
    }

    private void handleAtMyBean(Class c,Object obj) throws InvocationTargetException, IllegalAccessException {
        Method[]ms=c.getDeclaredMethods();
        for(Method m:ms){
            if(m.isAnnotationPresent(NyBean.class)){
                Object o=m.invoke(obj);
                //TODO：加入 处理 @MyBean注解对应的方法所实例化的类中的@MyPostConstruct对应的方法
                handlePostConstruct(o,o.getClass());//o指对象，o.getClass()他的反射对象
                beanMap.put(m.getName(),o);
            }
        }
    }

    private void handlePostConstruct(Object o, Class<?> aClass) throws InvocationTargetException, IllegalAccessException {
        Method[] ms=aClass.getDeclaredMethods();
        for(Method m:ms){
            if (m.isAnnotationPresent(MyPostConstruct.class)) {
                m.invoke(o);
            }
        }
    }
    //获得MyAppConfig的路径名
    private String[] getAppConfigBasePackages(Class c) {
        String[] paths=new String[1];
        paths[0]=c.getPackage().getName();
        return paths;
    }

    @Override
    public Object getBean(String id) {
        return beanMap.get(id);
    }
}

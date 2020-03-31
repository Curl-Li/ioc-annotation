package com.annotation.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Container {

    // 保存所有bean 格式为 类名 : bean
    private Map<String, Object> beans;

    // 存储对象和类名的关系 对象名 ：bean
    private Map<String, Object> beanKeys;

    private volatile static Container container=null;

    public static Container getInstance(){

        if(null==container){
            synchronized (Container.class){

                if(null==container){
                    container=new Container();
                }

            }
        }

        return container;

    }

    public Container() {
        beans = new ConcurrentHashMap<String, Object>();
        beanKeys=new ConcurrentHashMap<String,Object>();
    }

    /**
     * 以class的形式注册
     */
    public Object registerBean(Class<?> cls) {
        String className = cls.getName();
        Object bean = null;
        try {
            bean = cls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        beans.put(className, bean);

        return bean;
    }

    /**
     * 以bean的形式注册
     */
    public Object registerBean(Object bean) {
        String className = bean.getClass().getName();
        beans.put(className, bean);
        beanKeys.put(className, bean);
        return bean;
    }

    public List<Object> registerBean(List<Object> beans){

        List<Object> result=new ArrayList<>();

        for (Object o :
                beans) {
            result.add(registerBean(o));
        }
        return result;
    }


    /**
     * 通过 Class 对象获取bean
     */
    public <T> T getBean(Class<?> cls) {
        String className = cls.getName();
        Object object = beans.get(className);
        if (null != object) {
            return (T) object;
        }
        return null;
    }


    public boolean isExist(Class<?> cls){
        return null != beans.get(cls.getName());
    }


}

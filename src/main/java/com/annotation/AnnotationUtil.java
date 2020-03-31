package com.annotation;

import com.annotation.annotations.Autowired;
import com.annotation.annotations.Bean;
import com.annotation.annotations.Component;
import com.annotation.annotations.Value;
import com.annotation.enums.EnvironmentType;
import com.annotation.exceptions.AnnotationInjectionException;
import com.annotation.exceptions.ScanPackageException;
import com.annotation.exceptions.ValueInjectionException;
import com.annotation.model.Container;
import com.annotation.util.PropertiesUtils;
import com.annotation.util.ScanUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnnotationUtil {

    //包路径
    private static String packagePath;

    //程序主类
    private static Class<?> mainClass;

    //环境类型
    private static EnvironmentType environmentType=EnvironmentType.PRODUCTION;

    /**
     * 分析扫描包并将注解变量注入值
     * @throws ScanPackageException 包扫描异常
     * @throws AnnotationInjectionException 注解注入异常
     * @throws ValueInjectionException 值注入异常
     */
    private static void analyse() throws ScanPackageException, AnnotationInjectionException, ValueInjectionException {

        List<Class<?>> classes;

        //扫描路径下的类
        try {
            if(environmentType==EnvironmentType.DEVELOPMENT){
                classes=ScanUtil.scanClassDevelop(mainClass,packagePath);
            }else {
                classes=ScanUtil.scanClass(packagePath);
            }
        }catch (ClassNotFoundException | IOException ex){
            throw new ScanPackageException("包扫描异常",ex.getCause());
        }


        //扫描加载Bean加入容器
        for (Class<?> c :
                classes) {
            doAnalyseBean(c);
        }

        //扫描注入Bean加入容器
        for (Class<?> c :
                classes) {
            Object component=doAnalyseComponent(c);
            if(null!=component){
                Container.getInstance().registerBean(component);
            }
        }

    }

    /**
     * 分析Bean方法，将返回值加入容器
     * @param clz 需要分析的类
     * @throws AnnotationInjectionException 反射执行方法导致的异常
     */
    private static void doAnalyseBean(Class<?> clz) throws AnnotationInjectionException {

        List<Object> objects = new ArrayList<>();

        Method[] methods = clz.getMethods();

        for (Method method :
                methods) {

            if (method.isAnnotationPresent(Bean.class)) {
                try {
                    Object o=method.invoke(clz.newInstance());
                    objects.add(o);
                } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    throw new AnnotationInjectionException("Bean注入失败");
                }
            }
        }
        Container.getInstance().registerBean(objects);

    }

    /**
     * 分析Component注解，并将其有Value与Autowired注解的字段注入值
     * @param clz 需要分析的类
     * @return 注入值后的实例
     * @throws ValueInjectionException 值注入异常
     * @throws AnnotationInjectionException 注解注入异常
     */
    private static Object doAnalyseComponent(Class<?> clz) throws ValueInjectionException, AnnotationInjectionException {

        if (clz.isAnnotationPresent(Component.class)) {

            Container container=Container.getInstance();

            if(!container.isExist(clz)){

                Object obj;

                try {

                    obj=clz.newInstance();

                    doAnalyseValue(obj, clz);

                    doAnalyseAutowired(obj);

                }catch (InstantiationException | IllegalAccessException e) {
                    throw new AnnotationInjectionException("注解注入异常",e.getCause());
                }

                return container.registerBean(obj);

            }else {

                return container.getBean(clz);

            }

        }

        return null;

    }

    /**
     * 分析Value注解，注入值
     * @param obj 需要分析的类
     * @param clz 类
     * @throws ValueInjectionException 值注入异常
     */
    private static void doAnalyseValue(Object obj, Class<?> clz) throws ValueInjectionException {

        Field[] fields = clz.getDeclaredFields();

        for (Field field : fields) {
            boolean fieldHasAnno = field.isAnnotationPresent(Value.class);
            if (fieldHasAnno) {

                try {
                    Value valueAnnoation = field.getAnnotation(Value.class);
                    // 获取注解的值
                    String value = valueAnnoation.value();
                    // 通过注解设置的值获取properties的值
                    String propertiedValue = Objects.requireNonNull(PropertiesUtils.getInstance().get(value));
                    // 获取属性的名字
                    String name = field.getName();
                    // 将属性的首字符大写， 构造get，set方法
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    // 获取属性的类型
                    String type = field.getGenericType().toString();
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    // String 类型
                    if (type.equals("class java.lang.String")) {
                        Method m = clz.getMethod("set" + name, String.class);
                        // invoke方法传递实例对象，因为要对实例处理，而不是类
                        m.invoke(obj, propertiedValue);
                    }
                    // int Integer类型
                    if (type.equals("class java.lang.Integer")) {
                        Method m = clz.getMethod("set" + name, Integer.class);
                        m.invoke(obj, Integer.parseInt(propertiedValue));
                    }
                    if (type.equals("int")) {
                        Method m = clz.getMethod("set" + name, int.class);
                        m.invoke(obj, (int) Integer.parseInt(propertiedValue));
                    }
                    // boolean Boolean类型
                    if (type.equals("class java.lang.Boolean")) {
                        Method m = clz.getMethod("set" + name, Boolean.class);
                        if (propertiedValue.equalsIgnoreCase("true")) {
                            m.invoke(obj, true);
                        }
                        if (propertiedValue.equalsIgnoreCase("false")) {
                            m.invoke(obj, true);
                        }
                    }
                    if (type.equals("boolean")) {
                        Method m = clz.getMethod("set" + name, boolean.class);
                        if (propertiedValue.equalsIgnoreCase("true")) {
                            m.invoke(obj, true);
                        }
                        if (propertiedValue.equalsIgnoreCase("false")) {
                            m.invoke(obj, true);
                        }
                    }
                    // long Long 数据类型
                    if (type.equals("class java.lang.Long")) {
                        Method m = clz.getMethod("set" + name, Long.class);
                        m.invoke(obj, Long.parseLong(propertiedValue));
                    }
                    if (type.equals("long")) {
                        Method m = clz.getMethod("set" + name, long.class);
                        m.invoke(obj, Long.parseLong(propertiedValue));
                    }
                    // 时间数据类型
                    if (type.equals("class java.util.Date")) {
                        Method m = clz.getMethod("set" + name, java.util.Date.class);
                        m.invoke(obj, propertiedValue);
                    }
                }catch (FileNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                    throw new ValueInjectionException("值注入异常",e.getCause());
                }


            }
        }

    }

    /**
     * 分析Autowired注解并注入值 若所需Bean未加入容器，则递归处理
     * @param obj 需要分析的类
     * @throws ValueInjectionException 值注入异常
     * @throws AnnotationInjectionException 注解注入异常
     */
    private static void doAnalyseAutowired(Object obj) throws ValueInjectionException, AnnotationInjectionException {

        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {

            if (field.isAnnotationPresent(Autowired.class)) {

                // 要注入的字段
                Object autoWritedField = null;

                autoWritedField = Container.getInstance().getBean(field.getType());

                if (null == autoWritedField) {

                    autoWritedField=doAnalyseComponent(field.getType());

                }

                if (null == autoWritedField) {

                    throw new AnnotationInjectionException("注解注入异常"+field.getType().getCanonicalName()+" 为空");

                }

                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                try {
                    field.set(obj, autoWritedField);
                } catch (IllegalAccessException e) {
                    throw new AnnotationInjectionException("注解注入异常",e.getCause());
                }
                field.setAccessible(accessible);
            }
        }

    }

    /**
     * 初始化注解处理，指定主类，程序将扫描主类包下的所有包和类，默认为产品模式，即扫描Jar包中的类
     * @param mainClass 程序主类
     * @throws AnnotationInjectionException 注解注入异常
     * @throws ScanPackageException 包扫描异常
     * @throws ValueInjectionException 值注入异常
     */
    public static void init(Class<?> mainClass) throws AnnotationInjectionException, ScanPackageException, ValueInjectionException {
        AnnotationUtil.mainClass=mainClass;
        packagePath = mainClass.getPackage().getName();
        analyse();
    }

    /**
     * 初始化注解处理，指定包扫描路径，默认为产品模式，即扫描Jar包中的类
     * @param packagePath 包扫描路径
     * @throws AnnotationInjectionException 注解注入异常
     * @throws ScanPackageException 包扫描异常
     * @throws ValueInjectionException 值注入异常
     */
    public static void init(String packagePath) throws AnnotationInjectionException, ScanPackageException, ValueInjectionException {
        AnnotationUtil.packagePath = packagePath;
        analyse();
    }

    /**
     * 初始化注解处理，指定主类，程序将扫描主类包下的所有包和类，指定环境模式
     * @param environmentType 环境模式 产品和开发模式
     * @param mainClass 主类
     * @throws AnnotationInjectionException 注解注入异常
     * @throws ScanPackageException 包扫描异常
     * @throws ValueInjectionException 值注入异常
     */
    public static void init(EnvironmentType environmentType,Class<?> mainClass) throws AnnotationInjectionException, ScanPackageException, ValueInjectionException {
        AnnotationUtil.mainClass=mainClass;
        packagePath = mainClass.getPackage().getName();
        AnnotationUtil.environmentType=environmentType;
        analyse();
    }

    /**
     * 初始化注解处理，指定包扫描路径，指定环境模式
     * @param environmentType 环境模式 产品和开发模式
     * @param packagePath 主类
     * @throws AnnotationInjectionException 注解注入异常
     * @throws ScanPackageException 包扫描异常
     * @throws ValueInjectionException 值注入异常
     */
    public static void init(EnvironmentType environmentType,String packagePath) throws AnnotationInjectionException, ScanPackageException, ValueInjectionException {
        AnnotationUtil.packagePath = packagePath;
        AnnotationUtil.environmentType=environmentType;
        analyse();
    }

}
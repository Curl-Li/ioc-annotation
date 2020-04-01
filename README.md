# Java普通项目注解实现

By Curl-Li

## 项目概要

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
本项目主要采用反射机制及容器实现在 Java 普通项目中的部分注解。目前已实现`@Component @Bean @Value @Autowired`。

## 项目流程图

![rtmp](https://hfaisccenter.com:8080/rtmp.png)

## 使用说明

    1. 导入Jar包
    
    2. 实现 PropertiesHandler 接口
    
       public class PropertiesHandlerImpl implements PropertiesHandler {
           @Override
           public String get(String key) {
               return "";
           }
       }

    3. 定义扫描包路径并初始化
    
       AnnotationUtil.init(EnvironmentType.DEVELOPMENT,Main.class);
       
       EnvironmentType 为当前程序运行环境, DEVELOPMENT 为开发环境, PRODUCTION 为产品环境, 即 Jar 包运行环境
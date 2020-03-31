package com.annotation.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ScanUtil {


    public static List<Class<?>> scanClass(String basePackage) throws IOException, ClassNotFoundException {

        List<Class<?>> classes=new ArrayList<>();

        Enumeration<URL> urlEnumeration = Thread.currentThread().getContextClassLoader().getResources(basePackage.replace(".", "/"));

        while (urlEnumeration.hasMoreElements()) {

            URL url = urlEnumeration.nextElement();

            String protocol = url.getProtocol();

            if ("jar".equalsIgnoreCase(protocol)) {

                JarURLConnection connection = (JarURLConnection) url.openConnection();

                if (connection != null) {

                    JarFile jarFile = connection.getJarFile();

                    if (jarFile != null) {

                        Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();

                        while (jarEntryEnumeration.hasMoreElements()) {

                            JarEntry entry = jarEntryEnumeration.nextElement();

                            String jarEntryName = entry.getName();

                            //这里我们需要过滤不是class文件和不在basePack包名下的类
                            if (jarEntryName.contains(".class") && jarEntryName.replaceAll("/", ".").startsWith(basePackage)) {

                                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replace("/", ".");

                                Class<?> cls = Class.forName(className);

                                classes.add(cls);
                            }
                        }
                    }
                }
            }
        }

        return classes;
    }

    public static List<Class<?>> scanClassDevelop(Class<?> mainClass,String basePackage) throws ClassNotFoundException {

        List<Class<?>> classes=new ArrayList<>();

        //先把包名转换为路径,首先得到项目的classpath
        String classpath = mainClass.getResource("/").getPath();
        //然后把我们的包名basPach转换为路径名
        basePackage = basePackage.replace(".", File.separator);
        //然后把classpath和basePack合并
        String searchPath = classpath + basePackage;
        List<String> classPaths=doPath(new File(searchPath));
        //这个时候我们已经得到了指定包下所有的类的绝对路径了。我们现在利用这些绝对路径和java的反射机制得到他们的类对象
        for (String s : classPaths) {
            s = s.replace(classpath, "").replace(File.separator, ".").replace(".class", "");
            classes.add(Class.forName(s));
        }

        return classes;
    }

    private static List<String> doPath(File file) {
        List<String> paths=new ArrayList<>();
        if (file.isDirectory()) {//文件夹
            //文件夹我们就递归
            File[] files = file.listFiles();
            for (File f1 : files) {
                paths.addAll(doPath(f1));
            }
        } else {//标准文件
            //标准文件我们就判断是否是class文件
            if (file.getName().endsWith(".class")) {
                //如果是class文件我们就放入我们的集合中。
                paths.add(file.getPath());
            }
        }

        return paths;
    }

}

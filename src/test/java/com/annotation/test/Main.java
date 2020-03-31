package com.annotation.test;

import com.annotation.AnnotationUtil;
import com.annotation.annotations.Autowired;
import com.annotation.annotations.Component;
import com.annotation.enums.EnvironmentType;
import com.annotation.exceptions.AnnotationInjectionException;
import com.annotation.exceptions.ScanPackageException;
import com.annotation.exceptions.ValueInjectionException;
import com.annotation.util.PropertiesUtils;

@Component
public class Main {

    @Autowired
    private static TestService service;

    public static void main(String[] args) throws AnnotationInjectionException, ScanPackageException, ValueInjectionException {

        PropertiesUtils.setFilename("/Volumes/DATA/Project/Java/ioc-annotation/src/main/resources/config.yml");

        AnnotationUtil.init(EnvironmentType.DEVELOPMENT,Main.class);

        service.run();

    }

}

package com.annotation.test;

import com.annotation.AnnotationUtil;
import com.annotation.annotations.Autowired;
import com.annotation.annotations.Component;
import com.annotation.enums.EnvironmentType;
import com.annotation.exceptions.*;

@Component
public class Main {

    @Autowired
    private static TestService service;

    @Autowired
    private static TestBeanComponent testBeanComponent;

    public static void main(String[] args) throws AnnotationInjectionException, ScanPackageException, ValueInjectionException, PropertiesHandlerNotImplements, PropertiesHandlerLoadError {

        PropertiesHandlerImpl.setFilename("src/main/resources/config.yml");

        AnnotationUtil.init(EnvironmentType.DEVELOPMENT,Main.class);

        service.run();

        testBeanComponent.run();

    }

}

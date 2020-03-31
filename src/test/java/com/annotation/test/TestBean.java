package com.annotation.test;

import com.annotation.annotations.Bean;

public class TestBean {

    @Bean
    public TestBeanComponent testBeanComponent(){
        return new TestBeanComponent();
    }

}

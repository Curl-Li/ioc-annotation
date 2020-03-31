package com.annotation.test;

import com.annotation.annotations.Autowired;
import com.annotation.annotations.Component;

@Component
public class TestService {

    @Autowired
    private TestComponent component;

    public void run(){
        System.out.println(component.getName());
    }

}

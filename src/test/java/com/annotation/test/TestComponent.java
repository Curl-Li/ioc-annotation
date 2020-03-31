package com.annotation.test;

import com.annotation.annotations.Component;
import com.annotation.annotations.Value;

@Component
public class TestComponent {

    @Value(value = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

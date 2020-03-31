package com.annotation.exceptions;

public class ValueInjectionException extends Exception {

    public ValueInjectionException(String msg){
        super(msg);
    }

    public ValueInjectionException(String msg,Throwable cause){
        super(msg,cause);
    }

}

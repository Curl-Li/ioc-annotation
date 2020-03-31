package com.annotation.exceptions;

public class AnnotationInjectionException extends Exception {

    public AnnotationInjectionException(String msg){
        super(msg);
    }

    public AnnotationInjectionException(String msg,Throwable cause){
        super(msg,cause);
    }

}

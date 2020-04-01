package com.annotation.exceptions;

public class PropertiesHandlerNotImplements extends Exception {

    public PropertiesHandlerNotImplements(String msg){
        super(msg);
    }

    public PropertiesHandlerNotImplements(String msg,Throwable cause){
        super(msg,cause);
    }

}

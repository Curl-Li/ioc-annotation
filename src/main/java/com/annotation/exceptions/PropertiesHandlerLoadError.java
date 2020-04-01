package com.annotation.exceptions;

public class PropertiesHandlerLoadError extends Exception {

    public PropertiesHandlerLoadError(String msg){
        super(msg);
    }

    public PropertiesHandlerLoadError(String msg,Throwable cause){
        super(msg,cause);
    }


}

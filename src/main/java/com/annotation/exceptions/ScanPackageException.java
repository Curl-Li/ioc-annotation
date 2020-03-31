package com.annotation.exceptions;

public class ScanPackageException extends Exception {

    public ScanPackageException(String msg){
        super(msg);
    }

    public ScanPackageException(String msg,Throwable cause){
        super(msg,cause);
    }

}

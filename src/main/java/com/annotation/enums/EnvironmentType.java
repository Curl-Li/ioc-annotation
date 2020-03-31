package com.annotation.enums;

/**
 * 环境类型
 */
public enum EnvironmentType {

    /**
     * 开发环境
     */
    DEVELOPMENT(0),

    /**
     * 产品环境，检测jar路径
     */
    PRODUCTION(1);

    private int code;

    EnvironmentType(int code){
        this.code=code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

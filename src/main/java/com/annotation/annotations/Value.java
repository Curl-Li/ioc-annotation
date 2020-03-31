package com.annotation.annotations;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
// 此注解作用于字段上
@Target(ElementType.FIELD)
public @interface Value {

    String value();

}

package com.xujx.springmvc.annotation;

import java.lang.annotation.*;

/**
 * Created by xujinxin on 2017/8/7.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Resource {
    Class clazz();
}

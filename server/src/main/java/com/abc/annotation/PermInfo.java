package com.abc.annotation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PermInfo {

    /**
     * 权限值
     * @return
     */
    String pval() default "";

    /**
     * 权限名称
     * pname的别名
     * @return
     */
    String value() default "";

}

package com.xiaoyu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 表注在Bean中的注入属性上，如果一个接口有多个实现，通过此注解区分调用的实例
 * </p>
 *
 * @author ZhangXianYu   Email: 1600501744@qq.com
 * @since 2021/6/28 9:41
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Qualifier {

    String value() default "";
}

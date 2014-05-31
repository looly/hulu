package com.xiaoleilu.hulu.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 拦截器注解<br>
 * value: 拦截器列表<br>
 * 拦截器的执行顺序是按照给定拦截器数组的顺序来的<br>
 * 先执行Action类的拦截器，后执行方法的拦截器<br>
 * 拦截器都是单例存在的！
 * @author xiaoleilu
 *
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Intercept {
	Class<? extends Interceptor>[] value();
}

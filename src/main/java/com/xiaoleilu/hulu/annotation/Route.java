package com.xiaoleilu.hulu.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解，用于自定义访问的URL路径<br>
 * 值可以是一个请求路径，如果需要指定HTTP方法，在前面加方法名用":"分隔既可<br>
 * 例如："get:/test/testMethod"
 * @author loolly
 *
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Route {
	String value() default "";
}

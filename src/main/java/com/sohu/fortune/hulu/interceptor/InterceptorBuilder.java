package com.sohu.fortune.hulu.interceptor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import looly.github.hutool.Log;

import org.slf4j.Logger;

/**
 * 拦截器实例构建器<br>
 * 用于构建Action和Action方法的拦截器列表<br>
 * 所有拦截器都为单例模式
 * @author xiaoleilu
 *
 */
public class InterceptorBuilder{
	private static Logger log = Log.get();
	
	private static Map<Class<? extends Interceptor>, Interceptor> pool = new ConcurrentHashMap<Class<? extends Interceptor>, Interceptor>();
	
	/**
	 * 获得指定Action类的拦截器单例对象列表
	 * @param actionClass Action类
	 * @return
	 */
	public static Interceptor[] build(Class<?> actionClass) {
		return build(actionClass.getAnnotation(Intercept.class));
	}
	
	/**
	 * 获得指定Action类的拦截器单例对象列表
	 * @param actionMethod Action方法
	 * @return
	 */
	public static Interceptor[] build(Method actionMethod) {
		return build(actionMethod.getAnnotation(Intercept.class));
	}
	
	/**
	 * 实例化方法上的拦截器<br>
	 * 拦截器为单例
	 * 
	 * @param annotation Intercept注解
	 * @return
	 */
	public static Interceptor[] build(Intercept annotation) {
		if (annotation == null) {
			return null;
		}

		Class<? extends Interceptor>[] interceptorClasses = annotation.value();
		int count = interceptorClasses.length;
		if (count == 0) {
			return null;
		}

		Interceptor[] interceptors = new Interceptor[count];
		for (int i = 0; i < count; i++) {
			Interceptor interceptor = pool.get(interceptorClasses[i]);
			if (interceptor == null) {
				try {
					interceptor = interceptorClasses[i].newInstance();
					log.debug("New Interceptor: [{}]", interceptor.getClass().getName());
					pool.put(interceptorClasses[i], interceptor);
				} catch (Exception e) {
					log.error("Init interceptor error!", e);
				}
			}
			interceptors[i] = interceptor;
		}
		return interceptors;
	}
}

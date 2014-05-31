package com.xiaoleilu.hulu;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.xiaoleilu.hulu.annotation.Route;
import com.xiaoleilu.hulu.exception.ActionException;
import com.xiaoleilu.hulu.interceptor.Interceptor;
import com.xiaoleilu.hutool.StrUtil;

/**
 * Action方法<br>
 * 单例存在于容器中
 * 
 * @author xiaoleilu
 */
public class ActionMethod {

	/** 过滤器执行位置记录器 */
	private static ThreadLocal<Integer> interceptorPosition = new ThreadLocal<Integer>();

	private Object action;
	private Method method;
	private String requestPath;
	private Interceptor[] interceptors;

	// -------------------------------------------------------------------- Constructor start
	public ActionMethod(Object action, Method method) {
		this.action = action;
		this.method = method;
		
		this.method.setAccessible(true); // 取消安全检查，加快invoke速度

		// 生成请求路径
		this.requestPath = genRequestPath();
	}

	public ActionMethod(Object action, Method method, Interceptor[] interceptors) {
		this(action, method);
		this.interceptors = interceptors;
	}

	// -------------------------------------------------------------------- Constructor end

	// ------------------------------------------------------------- Setters and Getters start
	/**
	 * 获得请求路径
	 * 
	 * @return
	 */
	public String getRequestPath() {
		return requestPath;
	}

	/**
	 * 获得Action方法
	 * 
	 * @return
	 */
	protected Method getMethod() {
		return this.method;
	}

	// ------------------------------------------------------------- Setters and Getters end

	/**
	 * 执行Action方法<br>
	 * 同时会执行过滤器方法
	 * 
	 * @param urlParam URL参数
	 * @throws ActionException
	 */
	public void invoke() throws ActionException {
		Integer position = interceptorPosition.get();
		if (position == null) {
			position = 0;
		}

		if (interceptors != null) {
			if (position < interceptors.length) {
				interceptorPosition.set(position + 1);
				interceptors[position].invoke(this);
			}
		}

		if (interceptors == null || position == interceptors.length) {
			try {
				this.method.invoke(this.action);
			} catch(InvocationTargetException te) {
				throw new ActionException(te);
			} catch (Exception e) {
				throw new ActionException("Invoke action method error!", e);
			}

			// 执行了Action本体方法，说明过滤器使用完毕，清理游标防止重复执行
			resetInterceptorPosition();
		}

	}

	/**
	 * 重置过滤器执行顺序游标
	 */
	protected void resetInterceptorPosition() {
		interceptorPosition.remove();
	}

	// ------------------------------------------------------------- Private method start
	/**
	 * 生成请求路径
	 * 
	 * @return
	 */
	private String genRequestPath() {
		// 根据Annotation自定义请求路径
		String routePath = getRouteAnnotationPath(this.method);
		if(routePath != null) {
			return routePath;
		}

		final String actionName = StrUtil.removeSuffix(this.action.getClass().getSimpleName(), HuluSetting.actionSuffix).toLowerCase();
		return String.format("/%s/%s", actionName, this.method.getName());
	}

	/**
	 * 获得Route注解的自定义请求路径<br>
	 * 会自动在前加"/"，尾部去"/"
	 * @param method Action方法
	 * @return 处理后的请求路径，无定义为null
	 */
	private String getRouteAnnotationPath(Method method) {
		final Route routeAnnotation = method.getAnnotation(Route.class);
		if (null == routeAnnotation) {
			return null;
		}
		
		String routePath = routeAnnotation.value();
		if (StrUtil.isBlank(routePath)) {
			//index请求
			return "";
		}
		
		routePath = StrUtil.cleanBlank(routePath);
		routePath = StrUtil.removeSuffix(routePath, StrUtil.SLASH); // 去除尾部斜杠
		if (routePath.startsWith(StrUtil.SLASH) == false) {
			routePath = StrUtil.SLASH + routePath;
		}
		
		
		return routePath;
	}
	// ------------------------------------------------------------- Private method end
}

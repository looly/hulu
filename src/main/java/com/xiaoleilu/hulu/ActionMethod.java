package com.xiaoleilu.hulu;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.multipart.MultipartFormData;
import cn.hutool.extra.servlet.multipart.UploadFile;
import cn.hutool.json.JSON;
import cn.hutool.log.StaticLog;
import com.xiaoleilu.hulu.annotation.Param;
import com.xiaoleilu.hulu.annotation.Route;
import com.xiaoleilu.hulu.exception.ActionException;
import com.xiaoleilu.hulu.interceptor.Interceptor;
import com.xiaoleilu.hulu.view.DefaultView;
import com.xiaoleilu.hulu.view.View;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.List;

/**
 * Action方法<br>
 * 单例存在于容器中
 * 
 * @author xiaoleilu
 */
public class ActionMethod {
	/** 过滤器执行位置记录器 */
	private static ThreadLocal<Integer> interceptorPosition = new ThreadLocal<>();

	private Object action;						//Action对象
	private Method method;					//Action方法
	private String requestPath;				//请求路径
	private String httpMethod;				//HTTP方法（GET、POST等）
	private Interceptor[] interceptors;	//过滤器

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
	 * @return 请求路径
	 */
	public String getRequestPath() {
		return requestPath;
	}

	/**
	 * 获得Action方法
	 * 
	 * @return Action方法
	 */
	protected Method getMethod() {
		return this.method;
	}
	
	/**
	 * 获得Http请求的方法，例如GET，POST等
	 * @return HTTP方法
	 */
	protected String getHttpMethod() {
		return this.httpMethod;
	}

	// ------------------------------------------------------------- Setters and Getters end

	/**
	 * 执行Action方法<br>
	 * 同时会执行过滤器方法<br>
	 * 此方法为递归调用，每次递归调用此方法，都会判断执行到了第几个拦截器，从而执行拦截器。<br>
	 * 当拦截器数量执行完毕后，执行本体方法
	 * 
	 * @throws ActionException Action异常
	 */
	public void invoke() throws ActionException {
		Integer position = interceptorPosition.get();
		if (position == null) {
			position = 0;
		}

		if (interceptors != null && position < interceptors.length) {
			//执行过滤器，递归调用本方法
			interceptorPosition.set(position + 1);
			interceptors[position].invoke(this);
		}else {
			//过滤器执行完毕，执行本体方法
			invokeActionMethod();
			// 执行了Action本体方法，说明过滤器使用完毕，清理游标防止重复执行
			resetInterceptorPosition();
		}
	}
	
	/**
	 * 重置过滤器执行顺序游标<br>
	 * 游标记录了执行到了第几个过滤器
	 */
	protected void resetInterceptorPosition() {
		interceptorPosition.remove();
	}
	
	/**
	 * 指定用户请求的HTTP方法是否和定义的方法匹配<br>
	 * 用户只有在Route注解中定义方法后才会匹配有效性
	 * 
	 * @param method 客户端使用的Http方法
	 * @return 是否匹配
	 */
	protected boolean isHttpMethodMatch(String method) {
		if(StrUtil.isNotBlank(httpMethod) && httpMethod.equalsIgnoreCase(method) == false) {
			if(HuluSetting.isDevMode) {
				StaticLog.warn("Request [{}] method [{}] is not match [{}]", requestPath, method, httpMethod);
			}
			return false;
		}
		return true;
	}

	// ------------------------------------------------------------- Private method start
	/**
	 * 执行本体方法
	 * @throws ActionException  Action异常
	 */
	private void invokeActionMethod() throws ActionException {
		Object returnValue;
		Class<?>[] parameterTypes = this.method.getParameterTypes();
		try {
			returnValue = this.method.invoke(action, paramTypesToObj(parameterTypes));
		} catch(InvocationTargetException te) {
			throw new ActionException(te.getCause());
		} catch (Exception e) {
			throw new ActionException("Invoke action method error!", e);
		}
		
		//对于带有返回值的Action方法，执行Render
		if(null != returnValue) {
			if(false == (returnValue instanceof View)) {
				//将未识别响应对象包装为View
				returnValue = DefaultView.wrap(returnValue);
			}
			((View) returnValue).render();
		}
	}
	
	/**
	 * 通过参数类型列表生成参数值列表<br>
	 * 参数值列表为空时返回<code>null</code>
	 * @param paramTypes 参数类型列表
	 * @return 参数值列表
	 * @throws Exception 异常
	 */
	private Object[] paramTypesToObj(Class<?>[] paramTypes) throws Exception{
		if(ArrayUtil.isEmpty(paramTypes)){
			return new Object[]{};
		}
		
		//由于需要复杂的类型判断，因此无参方式的Action方法性能更好
		Object[] params = new Object[paramTypes.length];
		Class<?> paramType;
		Param paramAnnotation;
		String paramName;
		for(int i = 0; i < paramTypes.length; i++){
			paramType = paramTypes[i];
			paramAnnotation = paramType.getAnnotation(Param.class);
			if(null != paramAnnotation){
				//Annotation 参数注入
				paramName = paramAnnotation.value();
				if(StrUtil.isNotBlank(paramName)){
					if(CharSequence.class.isAssignableFrom(paramType)){
						//字符串
						params[i] = Request.getParam(paramName);
					}else if(Number.class.isAssignableFrom(paramType)){
						//数字
						params[i] = Request.getNumberParam(paramName, null);
					}else if(UploadFile.class.isAssignableFrom(paramType)){
						//上传文件
						params[i] = Request.getFileParam(paramName);
					}else if(Date.class.isAssignableFrom(paramType)){
						//日期
						params[i] = Request.getDateParam(paramName, null);
					}
				}
			}else{
				//类型注入
				if(Dict.class.isAssignableFrom(paramType)){
					//Dict对象参数
					params[i] = Request.fill((Dict)paramType.newInstance());
				}else if(ServletRequest.class.isAssignableFrom(paramType)){
					//ServletRequest
					params[i] = Request.getServletRequest();
				}else if(MultipartFormData.class.isAssignableFrom(paramType)){
					//MultipartFormData
					params[i] = Request.getMultipart();
				}else if(JSON.class.isAssignableFrom(paramType)){
					//JSON
					params[i] = Request.getJSONBody();
				}else if(ServletResponse.class.isAssignableFrom(paramType)){
					//ServletResponse
					params[i] = Response.getServletResponse();
				}else if(OutputStream.class.isAssignableFrom(paramType)){
					//OutputStream
					params[i] = Response.getOutputStream();
				}else if(Writer.class.isAssignableFrom(paramType)){
					//Writer
					params[i] = Response.getWriter();
				}else if(BeanUtil.isBean(paramType)){
					//JavaBean对象参数
					params[i] = Request.getBean(paramType);
				}
			}
		}
		return params;
	}
	
	/**
	 * 生成请求路径<br>
	 * 生成规则：<br>
	 * 1、获取Route注解的值，当方法名上的Route值为 / 开头表示绝对路径，忽略类的路径
	 * 2、不存在获取方法或者类本身的类名或方法名（类名去后缀后小写首字母）<br>
	 * 3、Route("post:")这种Route依旧使用方法名做为路径
	 * @return 请求路径
	 */
	private String genRequestPath() {
		//首先解析方法上的路径
		String[] httpMthodAndPath = getHttpMethodAndPath(this.method);
		this.httpMethod = httpMthodAndPath[0];
		String methodPath = httpMthodAndPath[1];
		
		if(StrUtil.isNotEmpty(methodPath) && methodPath.startsWith(StrUtil.SLASH)){
			return methodPath;
		}else{
			httpMthodAndPath = getHttpMethodAndPath(this.action.getClass());
			if(StrUtil.isBlank(this.httpMethod)){
				httpMethod = httpMthodAndPath[0];
			}
			final String actionPath = httpMthodAndPath[1];
			//Action路径
			return StrUtil.format("{}/{}", fixFirstSlash(actionPath), methodPath);
		}
	}
	
	/**
	 * 修正请求路径<br>
	 * 1、去除空白符
	 * 2、去除尾部斜杠
	 * @param path 原请求路径
	 * @return 修正后的请求路径
	 */
	private static String fixPath(String path) {
		if(StrUtil.isBlank(path)){
			return StrUtil.EMPTY;
		}
		path = StrUtil.cleanBlank(path);							//去除空白符
		path = StrUtil.removeSuffix(path, StrUtil.SLASH); // 去除尾部斜杠
		return path;
	}
	
	/**
	 * 补全第一个 /
	 * @param path 路径
	 * @return 补全的路径
	 */
	private static String fixFirstSlash(String path){
		if(StrUtil.isBlank(path)){
			return StrUtil.EMPTY;
		}
		
		if (false == path.startsWith(StrUtil.SLASH)) {
			path = StrUtil.SLASH + path;							//在路径前补全“/”
		}
		return path;
	}
	
	/**
	 * 获得Method对应的HTTP Method和Path<br>
	 * @param method Method对象
	 * @return {httpMethod, path}
	 */
	private static String[] getHttpMethodAndPath(Method method) {
		if(null == method){
			return null;
		}
		
		String[] httpMethodAndPath;
		Route routeAnnotation = method.getAnnotation(Route.class);
		if(null != routeAnnotation){//从Annotation中获取HttpMethod和路径
			httpMethodAndPath = getHttpMethodAndPath(routeAnnotation);
			if(null != httpMethodAndPath[0] && StrUtil.isBlank(httpMethodAndPath[1])){
				//对于"post:"这类Route，依旧使用方法名做为路径一部分
				httpMethodAndPath[1] = method.getName();
			}
		}else{
			httpMethodAndPath = new String[]{null, method.getName()};
		}
		return httpMethodAndPath;
	}
	
	/**
	 * 获得Action类对应的HTTP Method和Path<br>
	 * @param actionClass Action类
	 * @return {httpMethod, path}
	 */
	private static String[] getHttpMethodAndPath(Class<?> actionClass){
		if(null == actionClass){
			return null;
		}
		
		Route routeAnnotation = actionClass.getAnnotation(Route.class);
		if(null != routeAnnotation){
			return getHttpMethodAndPath(routeAnnotation);
		}else{
			return new String[]{null, StrUtil.lowerFirst(StrUtil.removeSuffix(actionClass.getSimpleName(), HuluSetting.actionSuffix))};
		}
	}
	
	/**
	 * 从Route Annotation的值中获取Http Method和Path<br>
	 * 例：
	 * 		Route("post:/example") -> {"POST", "/example"}
	 * 		Route("example") -> {null, "example"}
	 * 		Route("post:") -> {"POST", ""}
	 * @param route Route注解
	 * @return {httpMethod, path}
	 */
	private static String[] getHttpMethodAndPath(Route route){
		final String routeAnnotationValue = route.value();
		String httpMethod = null;
		String path;
		if(StrUtil.isNotBlank(routeAnnotationValue) && routeAnnotationValue.indexOf(":") > 0) {
			//处理带Http方法前缀的注解，类似于 "post:" 或者 "post:/example"
			final List<String> methodAndPath = StrUtil.split(routeAnnotationValue, ':', 2);
			httpMethod = methodAndPath.get(0).trim().toUpperCase();
			path = fixPath(methodAndPath.get(1).trim());
		}else{
			//处理直接的路径注解类似于 "/example"
			path = routeAnnotationValue;
		}
		return new String[]{httpMethod, path};
	}
	// ------------------------------------------------------------- Private method end
}

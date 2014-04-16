package com.sohu.fortune.hulu;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Action上下文<br>
 * 提供了request、response等的封装
 * @author xiaoleilu
 */
public class ActionContext {

	/** Request */
	private final static ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<HttpServletRequest>();
	/** Response */
	private final static ThreadLocal<HttpServletResponse> responseThreadLocal = new ThreadLocal<HttpServletResponse>();
	
	/** Servlet context */
	private static ServletContext servletContext;

	/**
	 * @return 获得当前线程的请求对象
	 */
	public final static HttpServletRequest getRequest() {
		return requestThreadLocal.get();
	}

	/**
	 * @return 获得当前线程的响应对象
	 */
	public final static HttpServletResponse getResponse() {
		return responseThreadLocal.get();
	}

	/**
	 * @return 获得项目的请求Path
	 */
	public static String getContextPath() {
		return servletContext.getContextPath();
	}
	
	/**
	 * @return 获得Servlet上下文
	 */
	public static ServletContext getServletContext(){
		return servletContext;
	}

	// ------------------------------------------------------------------------------------ Protected method start
	/**
	 * 填充Request对象和Response对象
	 * 
	 * @param req 请求对象
	 * @param res 响应对象
	 */
	protected final static void fillReqRes(HttpServletRequest req, HttpServletResponse res) {
		requestThreadLocal.set(req);
		responseThreadLocal.set(res);
	}

	/**
	 * 设置Servlet上下文
	 * @param context 上下文
	 */
	protected static void setServletContext(ServletContext context) {
		servletContext = context;
	}
	// ------------------------------------------------------------------------------------ Protected method end
}

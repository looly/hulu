package com.xiaoleilu.hulu;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;

/**
 * Action上下文<br>
 * 提供了request、response等的封装
 * 
 * @author xiaoleilu
 */
public class ActionContext {
	private static Log log = LogFactory.get();

	/** Servlet context */
	private static ServletContext servletContext;
	/** 请求处理对象 */
	private static ActionHandler handler;
	
	/**
	 * @return 获得Servlet上下文
	 */
	public static ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * @return 获得项目的请求Path
	 */
	public static String getContextPath() {
		return servletContext.getContextPath();
	}

	// ------------------------------------------------------------------------------------ Protected method start
	/**
	 * 初始化ActionContext
	 * 
	 * @param context ServletContext
	 */
	protected static void init(ServletContext context) {
		setServletContext(context);
		createActionHandler();
	}
	
	/**
	 * 注入ServletRequst和ServletResponse
	 * @param req ServletRequest
	 * @param res ServletResponse
	 */
	protected static void fillReqAndRes(ServletRequest req, ServletResponse res){
		//-- 填充请求和响应对象到ActionContext本地线程
		Request.init((HttpServletRequest)req);
		Response.init((HttpServletResponse)res);
	}
	
	/**
	 * 处理请求
	 * @return 
	 */
	protected static boolean handle() {
		return handler.handle();
	}

	// ------------------------------------------------------------------------------------ Protected method end

	// ------------------------------------------------------------------------------------ Private method start
	/**
	 * 初始化Servlet上下文<br>
	 * 
	 * @param context 上下文
	 */
	private static void setServletContext(ServletContext context) {
		servletContext = context;
		log.debug("Web app ContextPath [{}]", servletContext.getContextPath());
	}

	/**
	 * 创建Action映射
	 */
	private static void createActionHandler() {
		handler = new ActionHandler(HuluSetting.actionPackages);
	}
	// ------------------------------------------------------------------------------------ Private method end
}

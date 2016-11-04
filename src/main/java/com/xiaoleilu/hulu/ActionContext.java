package com.xiaoleilu.hulu;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import com.xiaoleilu.hutool.util.DateUtil;

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
	private static String contextPath;
	
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
		return contextPath;
	}

	// ------------------------------------------------------------------------------------ Protected method start
	/**
	 * 初始化ActionContext
	 * 
	 * @param context ServletContext
	 */
	protected static void init(ServletContext context) {
		final long start = System.currentTimeMillis();
		
		servletContext = context;
		contextPath = context.getContextPath();
		handler = new ActionHandler(HuluSetting.actionPackages);
		
		log.info("***** Hulu framwork init finished, context path: {}, spend {}ms *****", contextPath, DateUtil.spendMs(start));
	}
	
	/**
	 * 注入ServletRequest 和 ServletResponse并处理请求
	 * @param req ServletRequest
	 * @param res ServletResponse
	 * @return 是否处理成功
	 */
	protected static boolean handle(ServletRequest req, ServletResponse res) {
		return handler.handle((HttpServletRequest)req, (HttpServletResponse)res);
	}

	/**
	 * 销毁ActionContext
	 */
	protected static void destroy() {
		log.info("***** Hulu framwork stoped. *****");
	}
	// ------------------------------------------------------------------------------------ Protected method end

	// ------------------------------------------------------------------------------------ Private method start
	// ------------------------------------------------------------------------------------ Private method end
}

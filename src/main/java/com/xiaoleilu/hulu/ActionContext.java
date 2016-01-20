package com.xiaoleilu.hulu;

import javax.servlet.ServletContext;

import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import com.xiaoleilu.hutool.util.StrUtil;

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

	/** 项目路径的长度，用于请求时去掉项目路径 */
	protected static int contextPathLength;

	/** 请求处理对象 */
	protected static ActionHandler handler;

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
		return getServletContext().getContextPath();
	}

	// ------------------------------------------------------------------------------------ Protected method start
	/**
	 * 初始化ActionContext
	 * 
	 * @param context ServletContext
	 */
	protected static void init(ServletContext context) {
		initServletContext(context);
		createActionHandler();
	}

	// ------------------------------------------------------------------------------------ Protected method end

	// ------------------------------------------------------------------------------------ Private method start
	/**
	 * 初始化Servlet上下文<br>
	 * 
	 * @param context 上下文
	 */
	private static void initServletContext(ServletContext context) {
		servletContext = context;

		final String contextPath = servletContext.getContextPath();
		contextPathLength = (null == contextPath || StrUtil.SLASH.equals(contextPath) ? 0 : contextPath.length());
		log.debug("Web app ContextPath [{}]", contextPath);
	}

	/**
	 * 创建Action映射
	 */
	private static void createActionHandler() {
		handler = new ActionHandler(HuluSetting.actionPackages);
	}
	// ------------------------------------------------------------------------------------ Private method end
}

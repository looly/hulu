package com.sohu.fortune.hulu;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import looly.github.hutool.DateUtil;
import looly.github.hutool.HttpUtil;
import looly.github.hutool.Log;
import looly.github.hutool.StrUtil;

import org.slf4j.Logger;

/**
 * Action过滤器<br>
 * 引入了Servlet3.0的WebFilter注解，这样一旦引入Jar包，框架即可生效<br>
 * @author xiaoleilu
 */
@WebFilter(urlPatterns={"/*"})
public class ActionFilter implements Filter{
	private final static Logger log = Log.get();
	
	/** 用户自定义字符集的参数名称 */
	private final static String PARAM_NAME_CHARSET = "charset";
	
	/** 项目路径的长度，用于请求时去掉项目路径 */
	private int contextPathLength;
	
	/** 请求处理对象 */
	private ActionHandler handler;

	/**
	 * 框架启动初始化
	 */
	@Override
	public void  init(FilterConfig filterConfig) throws ServletException {
		final long start = System.currentTimeMillis();
		
		ActionContext.setServletContext(filterConfig.getServletContext());
		
		final String contextPath = ActionContext.getContextPath();
		this.contextPathLength = (null == contextPath || StrUtil.SLASH.equals(contextPath) ? 0 : contextPath.length());
		log.debug("Web app ContextPath [{}]", contextPath);
		
		handler = new ActionHandler(HuluSetting.actionPackages);
		
		log.info("***** Hulu framwork init fiished, spend {}ms *****", DateUtil.spendMs(start));
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		
		//-- 字符集的过滤
		String charset = req.getParameter(PARAM_NAME_CHARSET);
		if(StrUtil.isBlank(charset)) {
			charset = HuluSetting.charset;
		}
		
		try {
			request.setCharacterEncoding(charset);
			response.setCharacterEncoding(charset);
		} catch (Exception e) {
			log.warn("User [{}] use charset [{}] not support!", HttpUtil.getClientIP(request), charset);
		}

		//-- 填充请求和响应对象到ActionContext本地线程
		ActionContext.fillReqRes(request, response);
		
		//-- 处理Target
		String target = request.getRequestURI();
		//去掉项目名部分
		target = target.substring(contextPathLength);
		//如果以"/"结尾则去之
		target = StrUtil.removeSuffix(target, StrUtil.SLASH);
		
		//-- 处理请求
		if(handler.handle(target)) {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
	}

}

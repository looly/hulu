package com.xiaoleilu.hulu;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import com.xiaoleilu.hutool.util.DateUtil;

/**
 * Action过滤器<br>
 * 引入了Servlet3.0的WebFilter注解，这样一旦引入Jar包，框架即可生效<br>
 * @author xiaoleilu
 */
@WebFilter(urlPatterns={"/*"})
public class ActionFilter implements Filter{
	private static Log log = LogFactory.get();
	
	/**
	 * 框架启动初始化
	 * @param filterConfig FilterConfig
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		final long start = System.currentTimeMillis();
		ActionContext.init(filterConfig.getServletContext());
		log.info("***** Hulu framwork init finished, spend {}ms *****", DateUtil.spendMs(start));
	}

	/**
	 * 拦截请求
	 * @param req ServletRequest
	 * @param res ServletResponse
	 * @param chain FilterChain
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		ActionContext.fillReqAndRes(req, res);
		
		//-- 处理请求
		if(ActionContext.handle()) {
			chain.doFilter(req, res);
		}
	}

	/**
	 * 容器关闭
	 */
	@Override
	public void destroy() {
		log.info("***** Hulu framwork stoped. *****");
	}
}

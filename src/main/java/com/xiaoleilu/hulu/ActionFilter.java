package com.xiaoleilu.hulu;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * Action过滤器<br>
 * 引入了Servlet3.0的WebFilter注解，这样一旦引入Jar包，框架即可生效<br>
 * @author xiaoleilu
 */
@WebFilter(urlPatterns={"/*"})
public class ActionFilter implements Filter{
	
	/**
	 * 框架启动初始化
	 * @param filterConfig FilterConfig
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ActionContext.init(filterConfig.getServletContext());
	}

	/**
	 * 拦截请求
	 * @param req ServletRequest
	 * @param res ServletResponse
	 * @param chain FilterChain
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		//-- 处理请求，如果处理失败（无对应的Action），继续后续步骤
		if(false == ActionContext.handle(req, res)) {
			chain.doFilter(req, res);
		}
	}

	/**
	 * 容器关闭
	 */
	@Override
	public void destroy() {
		ActionContext.destroy();
	}
}

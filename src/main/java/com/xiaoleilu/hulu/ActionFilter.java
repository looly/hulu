package com.xiaoleilu.hulu;

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

import com.xiaoleilu.hutool.StrUtil;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.StaticLog;

/**
 * Action过滤器<br>
 * 引入了Servlet3.0的WebFilter注解，这样一旦引入Jar包，框架即可生效<br>
 * @author xiaoleilu
 */
@WebFilter(urlPatterns={"/*"})
public class ActionFilter implements Filter{
	private static Log log = StaticLog.get();
	
	/**
	 * 框架启动初始化
	 */
	@Override
	public void  init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		
		//-- 填充请求和响应对象到ActionContext本地线程
		ActionContext.fillReqRes(request, response);
		
		//-- 处理请求
		if(ActionContext.handler.handle(getWellFormTarget(request.getRequestURI()))) {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
		log.debug("***** ActionFilter over. *****");
	}
	
	// ------------------------------------------------------------------------------------ Private method start
	/**
	 * 处理将请求路径标准化为框架目标路径
	 * @param requestURI 请求URI
	 * @return 框架目标路径
	 */
	private static String getWellFormTarget(String target) {
		//去掉项目名部分
		target = target.substring(ActionContext.contextPathLength);
		//如果以"/"结尾则去之
		if(StrUtil.isNotEmpty(target) && false ==target.equals(StrUtil.SLASH)) {
			target = StrUtil.removeSuffix(target, StrUtil.SLASH);
		}
		
		return target;
	}
	// ------------------------------------------------------------------------------------ Private method end
}

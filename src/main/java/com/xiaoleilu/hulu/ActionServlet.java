package com.xiaoleilu.hulu;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet 入口
 * @author Looly
 *
 */
public class ActionServlet extends HttpServlet{
	private static final long serialVersionUID = -1429875457807724161L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		ActionContext.init(config.getServletContext());
		super.init(config);
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		//-- 处理请求，如果处理失败（无对应的Action），执行父类逻辑
		if(false == ActionContext.handle(req, res)) {
			super.service(req, res);
		}
	}
	
	@Override
	public void destroy() {
		super.destroy();
		ActionContext.destroy();
	}
}

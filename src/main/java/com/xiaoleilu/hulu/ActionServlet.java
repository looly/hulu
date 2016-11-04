package com.xiaoleilu.hulu;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import com.xiaoleilu.hutool.util.DateUtil;

/**
 * Servlet 入口
 * @author Looly
 *
 */
public class ActionServlet extends HttpServlet{
	private static final long serialVersionUID = -1429875457807724161L;
	private static Log log = LogFactory.get();

	@Override
	public void init(ServletConfig config) throws ServletException {
		final long start = System.currentTimeMillis();
		ActionContext.init(config.getServletContext());
		log.info("***** Hulu framwork init finished, spend {}ms *****", DateUtil.spendMs(start));
		super.init(config);
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		ActionContext.fillReqAndRes(req, res);
		
		//-- 处理请求
		if(ActionContext.handle()) {
			super.service(req, res);
		}
	}
	
	@Override
	public void destroy() {
		super.destroy();
		log.info("***** Hulu framwork stoped. *****");
	}
}

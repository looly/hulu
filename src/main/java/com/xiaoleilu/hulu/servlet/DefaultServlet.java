package com.xiaoleilu.hulu.servlet;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 默认Servlet<br>
 * @author Looly
 *
 */
public class DefaultServlet extends HttpServlet{
	private static final long serialVersionUID = -8137983239382306470L;
	
	private Servlet targetDefaultServlet;

	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			targetDefaultServlet = new org.apache.catalina.servlets.DefaultServlet();
		} catch (Exception e) {
			targetDefaultServlet = new org.eclipse.jetty.servlet.DefaultServlet();
		}
		targetDefaultServlet.init(config);
		super.init(config);
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		if(null != targetDefaultServlet){
			targetDefaultServlet.service(req, res);
		}else{
			super.service(req, res);
		}
	}
	
	@Override
	public void destroy() {
	}
}

package com.xiaoleilu.hulu.view;

import javax.servlet.http.HttpServletResponse;

/**
 * 500错误视图
 * @author Looly
 *
 */
public class Error500View extends ErrorView{
	//---------------------------------------------------------- Constructor start
	public Error500View() {
		this(null, null);
	}
	
	/**
	 * 构造
	 * @param errorMsg 异常消息
	 */
	public Error500View(String errorMsg) {
		this(null, errorMsg);
	}
	
	/**
	 * 构造
	 * @param e 异常
	 */
	public Error500View(Throwable e) {
		this(e, "500 Server Error!");
	}

	/**
	 * 构造
	 * @param e 异常
	 * @param errorMsg 异常消息
	 */
	public Error500View(Throwable e, String errorMsg) {
		super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, errorMsg);
	}
	//---------------------------------------------------------- Constructor end
	
	//---------------------------------------------------------- Getters and Setters start
	//---------------------------------------------------------- Getters and Setters end
}

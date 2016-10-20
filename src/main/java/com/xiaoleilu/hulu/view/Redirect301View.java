package com.xiaoleilu.hulu.view;

import javax.servlet.http.HttpServletResponse;

import com.xiaoleilu.hulu.ActionContext;
import com.xiaoleilu.hulu.Request;
import com.xiaoleilu.hulu.Response;
import com.xiaoleilu.hutool.util.StrUtil;

/**
 * 服务器端跳转
 * @author Looly
 *
 */
public class Redirect301View extends RedirectView{
	
	private boolean isWithParamStr;
	
	//---------------------------------------------------------- Constructor start
	public Redirect301View() {
		this(null);
	}
	
	public Redirect301View(String url) {
		this(url, true);
	}
	
	public Redirect301View(String url, boolean isWithParamStr) {
		super(url);
		this.isWithParamStr = isWithParamStr;
	}
	
	//---------------------------------------------------------- Constructor end
	
	//---------------------------------------------------------- Getters and Setters start
	public boolean isWithParamStr() {
		return isWithParamStr;
	}
	public void setWithParamStr(boolean isWithParamStr) {
		this.isWithParamStr = isWithParamStr;
	}
	//---------------------------------------------------------- Getters and Setters end

	@Override
	public void render() {
		String uri = this.getUri();
		String contextPath = ActionContext.getContextPath();
		if (contextPath != null && uri.indexOf("://") == -1) {
			uri = contextPath + uri;
		}

		if (isWithParamStr) {
			// 加入请求参数
			String paramStr = Request.getServletRequest().getQueryString();
			if (paramStr != null) uri = uri + "?" + paramStr;
		}

		Response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		Response.setHeader("Location", uri);
		Response.setHeader("Connection", "close");
	}
	
	@Override
	public String toString() {
		return StrUtil.format("Forward to: {}", this.getUri());
	}
}

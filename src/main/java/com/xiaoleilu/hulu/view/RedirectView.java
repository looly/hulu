package com.xiaoleilu.hulu.view;

import java.io.IOException;

import com.xiaoleilu.hulu.ActionContext;
import com.xiaoleilu.hulu.Response;
import com.xiaoleilu.hulu.exception.RenderException;
import com.xiaoleilu.hutool.util.StrUtil;

/**
 * 重定向跳转
 * 
 * @author Looly
 *
 */
public class RedirectView implements View {

	private String uri;

	// ---------------------------------------------------------- Constructor start
	public RedirectView() {
	}

	public RedirectView(String url) {
		this.uri = url;
	}
	// ---------------------------------------------------------- Constructor end

	// ---------------------------------------------------------- Getters and Setters start
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	// ---------------------------------------------------------- Getters and Setters end

	@Override
	public void render() {
		uri = ActionContext.getContextPath() + uri;
		try {
			Response.getServletResponse().sendRedirect(uri);
		} catch (IOException e) {
			throw new RenderException("Redirect to [" + uri + "] error!", e);
		}
	}

	@Override
	public String toString() {
		return StrUtil.format("Redirect to: {}", this.uri);
	}
}

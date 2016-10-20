package com.xiaoleilu.hulu.view;

import javax.servlet.http.HttpServletRequest;

import com.xiaoleilu.hulu.Request;
import com.xiaoleilu.hulu.Response;
import com.xiaoleilu.hulu.exception.RenderException;
import com.xiaoleilu.hutool.util.StrUtil;

/**
 * 服务器端跳转
 * @author Looly
 *
 */
public class ForwardView implements View{
	
	private String uri;
	
	//---------------------------------------------------------- Constructor start
	public ForwardView() {
	}
	
	public ForwardView(String url) {
		this.uri = url;
	}
	//---------------------------------------------------------- Constructor end
	
	//---------------------------------------------------------- Getters and Setters start
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	//---------------------------------------------------------- Getters and Setters end

	@Override
	public void render() {
		try {
			final HttpServletRequest request = Request.getServletRequest();
			request.getRequestDispatcher(uri).forward(request, Response.getServletResponse());
		} catch (Exception e) {
			throw new RenderException("Forward to " + uri + " error!", e);
		}
	}
	
	@Override
	public String toString() {
		return StrUtil.format("Forward to: {}", this.uri);
	}
}

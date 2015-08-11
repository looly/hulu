package com.xiaoleilu.hulu.render.view;

import com.xiaoleilu.hulu.exception.RenderException;
import com.xiaoleilu.hulu.render.Render;

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
			Render.forward(uri);
		} catch (Exception e) {
			throw new RenderException("Forward to " + uri + " error!", e);
		}
	}
	
	
}

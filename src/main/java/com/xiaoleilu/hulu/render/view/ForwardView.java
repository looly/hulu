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
	
	public ForwardView(String url) {
		this.uri = url;
	}

	@Override
	public void render() {
		try {
			Render.forward(uri);
		} catch (Exception e) {
			throw new RenderException("Forward to " + uri + " error!", e);
		}
	}
	
	
}

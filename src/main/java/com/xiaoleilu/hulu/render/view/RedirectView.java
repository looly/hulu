package com.xiaoleilu.hulu.render.view;

import com.xiaoleilu.hulu.render.Render;

/**
 * 重定向跳转
 * @author Looly
 *
 */
public class RedirectView implements View{
	
	private String uri;
	
	public RedirectView(String url) {
		this.uri = url;
	}

	@Override
	public void render() {
		Render.redirect(uri);
	}
}

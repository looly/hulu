package com.xiaoleilu.hulu.render.view;

import com.xiaoleilu.hulu.render.Render;
import com.xiaoleilu.hutool.util.StrUtil;

/**
 * 重定向跳转
 * @author Looly
 *
 */
public class RedirectView implements View{
	
	private String uri;
	
	//---------------------------------------------------------- Constructor start
	public RedirectView() {
	}
	
	public RedirectView(String url) {
		this.uri = url;
	}
	//---------------------------------------------------------- Constructor end

	@Override
	public void render() {
		Render.redirect(uri);
	}
	
	@Override
	public String toString() {
		return StrUtil.format("Redirect to: {}", this.uri);
	}
}

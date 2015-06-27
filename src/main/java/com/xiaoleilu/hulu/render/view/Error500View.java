package com.xiaoleilu.hulu.render.view;

import com.xiaoleilu.hulu.render.ErrorRender;

/**
 * 500错误视图
 * @author Looly
 *
 */
public class Error500View implements View{
	
	private Throwable e;

	public Error500View(Throwable e) {
		this.e = e;
	}

	@Override
	public void render() {
		ErrorRender.render500(e);
	}
}

package com.xiaoleilu.hulu.render.view;

import com.xiaoleilu.hulu.render.ErrorRender;

/**
 * 错误视图
 * @author Looly
 *
 */
public class ErrorView implements View{
	
	private int errorCode;
	private String errorContent;
	
	/**
	 * 构造
	 */
	public ErrorView() {
	}
	
	/**
	 * 构造
	 * @param errorCode 错误代码
	 * @param errorContent 错误内容
	 */
	public ErrorView(int errorCode, String errorContent) {
		this.errorCode = errorCode;
		this.errorContent = errorContent;
	}

	@Override
	public void render() {
		ErrorRender.render(errorCode, errorContent);
	}

}

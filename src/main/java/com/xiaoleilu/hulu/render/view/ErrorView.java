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
	
	//---------------------------------------------------------- Constructor start
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
	//---------------------------------------------------------- Constructor end
	
	//---------------------------------------------------------- Getters and Setters start
	/**
	 * @return 错误代码
	 */
	public int getErrorCode() {
		return errorCode;
	}
	/**
	 * 设置错误代码
	 * @param errorCode 错误代码
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return 错误内容
	 */
	public String getErrorContent() {
		return errorContent;
	}
	/**
	 * 设置错误内容
	 * @param errorContent 错误内容
	 */
	public void setErrorContent(String errorContent) {
		this.errorContent = errorContent;
	}
	//---------------------------------------------------------- Getters and Setters end

	@Override
	public void render() {
		ErrorRender.render(errorCode, errorContent);
	}

}

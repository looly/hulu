package com.xiaoleilu.hulu.render.view;

import com.xiaoleilu.hulu.render.Render;

/**
 * Javascript内容<br>
 * 自动获取callback参数，并返回以callback参数值命名的函数
 * @author Looly
 *
 */
public class JsView extends ContentTypeView{
	
	private String callbackParamName;
	
	//---------------------------------------------------------- Constructor start
	/**
	 * 构造，自定义参数
	 */
	public JsView() {
	}
	
	/**
	 * 构造，使用默认的回调参数名
	 * @param text 内容
	 */
	public JsView(String text) {
		this.text = text;
	}
	
	/**
	 * 构造
	 * @param text 内容
	 * @param callbackParamName 回调参数名
	 */
	public JsView(String text, String callbackParamName) {
		this.text = text;
		this.callbackParamName = callbackParamName;
	}
	//---------------------------------------------------------- Constructor end
	
	//---------------------------------------------------------- Getters and Setters start
	/**
	 * @return 回调参数名
	 */
	public String getCallbackParamName() {
		return callbackParamName;
	}
	/**
	 * 设置回调参数名
	 * @param callbackParamName 回调参数名
	 */
	public void setCallbackParamName(String callbackParamName) {
		this.callbackParamName = callbackParamName;
	}
	//---------------------------------------------------------- Getters and Setters end
	
	@Override
	public void render() {
		Render.renderJs(text, callbackParamName);
	}
}

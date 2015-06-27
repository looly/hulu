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
	
	@Override
	public void render() {
		Render.renderJs(text, callbackParamName);
	}
}

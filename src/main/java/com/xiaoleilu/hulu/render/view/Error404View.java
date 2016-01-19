package com.xiaoleilu.hulu.render.view;

import com.xiaoleilu.hulu.render.ErrorRender;
import com.xiaoleilu.hutool.util.StrUtil;


/**
 * 404页面
 * @author Looly
 *
 */
public class Error404View implements View{
	
	private String text;
	private boolean isHtml;
	
	//---------------------------------------------------------- Constructor start
	/**
	 * 构造，默认404页面
	 */
	public Error404View() {
	}
	
	/**
	 * 构造
	 * @param text 404页面显示的内容
	 * @param isHtml 是否为HTML页面，如果是，返回一个自定义的HTML页面，内容为text
	 */
	public Error404View(String text, boolean isHtml) {
		this.text = text;
		this.isHtml = isHtml;
	}
	
	/**
	 * 构造，默认带内容的404页面
	 * @param text 内容
	 */
	public Error404View(String text) {
		this(text, false);
	}
	//---------------------------------------------------------- Constructor end
	
	//---------------------------------------------------------- Getters and Setters start
	/**
	 * @return 内容
	 */
	public String getText() {
		return text;
	}
	/**
	 * 设置内容
	 * @param text 内容
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return 是否为HTML页面
	 */
	public boolean isHtml() {
		return isHtml;
	}
	/**
	 * 设置是否为HTML页面
	 * @param isHtml 是否为HTML 页面
	 */
	public void setHtml(boolean isHtml) {
		this.isHtml = isHtml;
	}
	//---------------------------------------------------------- Getters and Setters end
	
	@Override
	public void render() {
		if(StrUtil.isBlank(text)) {
			ErrorRender.render404();
		}else if(isHtml){
			ErrorRender.render404Page(text);
		}else {
			ErrorRender.render404(text);
		}
	}
}

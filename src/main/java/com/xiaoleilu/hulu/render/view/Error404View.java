package com.xiaoleilu.hulu.render.view;

import com.xiaoleilu.hulu.render.ErrorRender;
import com.xiaoleilu.hutool.StrUtil;


/**
 * 404页面
 * @author Looly
 *
 */
public class Error404View implements View{
	
	private String text;
	private boolean isHtml;
	
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
	
	public Error404View(String text) {
		this(text, false);
	}
	
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

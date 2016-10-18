package com.xiaoleilu.hulu.render.view;

import javax.servlet.http.HttpServletResponse;

import com.xiaoleilu.hulu.Response;


/**
 * 404页面
 * @author Looly
 *
 */
public class Error404View extends ErrorView{
	
	public static final String TEXT_DEFAULT = "404 Not Found!";
	
	private boolean isHtml;
	
	//---------------------------------------------------------- Constructor start
	/**
	 * 构造，默认404页面
	 */
	public Error404View() {
		this(TEXT_DEFAULT);
	}
	
	/**
	 * 构造，默认带内容的404页面
	 * @param text 内容
	 */
	public Error404View(String text) {
		this(text, false);
	}
	
	/**
	 * 构造
	 * @param errorContent 404页面显示的内容
	 * @param isHtml 是否为HTML页面，如果是，返回一个自定义的HTML页面，内容为text
	 */
	public Error404View(String errorContent, boolean isHtml) {
		super(HttpServletResponse.SC_NOT_FOUND, errorContent);
		this.isHtml = isHtml;
	}
	
	//---------------------------------------------------------- Constructor end
	
	//---------------------------------------------------------- Getters and Setters start
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
		String text = this.getErrorContent();
		if(isHtml){
			Response.setStatus(getErrorCode());
			new HtmlView(text).render();
		}else{
			super.render();
		}
	}
	
	@Override
	public String toString() {
		return getErrorContent();
	}
}

package com.xiaoleilu.hulu.view;

import com.xiaoleilu.hulu.Response;
import com.xiaoleilu.hutool.util.StrUtil;

/**
 * 自定义Content-Type的视图
 * @author Looly
 *
 */
public class ContentTypeView implements View{
	
	protected String text;
	protected String contentType;
	
	//---------------------------------------------------------- Constructor start
	public ContentTypeView() {
	}
	
	public ContentTypeView(String text, String contentType) {
		this.text = text;
		this.contentType = contentType;
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
	 * @return Content-Type
	 */
	public String getContentType() {
		return contentType;
	}
	/**
	 * 设置Content-Type
	 * @param contentType Content-Type
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	//---------------------------------------------------------- Getters and Setters end
	
	@Override
	public void render() {
		Response.write(text, contentType);
	}
	
	@Override
	public String toString() {
		return StrUtil.format("[{}] {}", this.contentType, this.text);
	}
}

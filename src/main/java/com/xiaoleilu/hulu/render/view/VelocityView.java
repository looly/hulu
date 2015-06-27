package com.xiaoleilu.hulu.render.view;

import com.xiaoleilu.hulu.render.Render;

/**
 * Velocity内容<br>
 * @author Looly
 *
 */
public class VelocityView extends ContentTypeView{
	
	private String templateFileName;
	
	/**
	 * 构造，默认返回HTML
	 * @param templateFileName 模板文件名
	 */
	public VelocityView(String templateFileName) {
		this.templateFileName = templateFileName;
		this.contentType = Render.CONTENT_TYPE_HTML;
	}
	
	/**
	 * 构造
	 * @param templateFileName 模板文件名
	 * @param contentType 返回内容类型
	 */
	public VelocityView(String templateFileName, String contentType) {
		this.templateFileName = templateFileName;
		this.contentType = contentType;
	}
	
	@Override
	public void render() {
		Render.renderVelocity(templateFileName, contentType);
	}
}

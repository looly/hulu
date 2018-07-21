package com.xiaoleilu.hulu.view.extra;

import javax.servlet.http.HttpServletResponse;

import com.xiaoleilu.hulu.Request;
import com.xiaoleilu.hulu.Response;
import com.xiaoleilu.hulu.view.View;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.engine.velocity.VelocityUtil;

/**
 * Velocity内容<br>
 * @author Looly
 *
 */
public class VelocityView implements View{
	
	private String templateFileName;
	private String contentType;
	
	//---------------------------------------------------------- Constructor start
	public VelocityView() {
	}
	
	/**
	 * 构造，默认返回HTML
	 * @param templateFileName 模板文件名
	 */
	public VelocityView(String templateFileName) {
		this.templateFileName = templateFileName;
		this.contentType = Response.CONTENT_TYPE_HTML;
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
	//---------------------------------------------------------- Constructor start
	
	//---------------------------------------------------------- Getters and Setters start
	/**
	 * @return 模板文件名
	 */
	public String getTemplateFileName() {
		return templateFileName;
	}
	/**
	 * 设置模板文件名
	 * @param templateFileName 模板文件名
	 */
	public void setTemplateFileName(String templateFileName) {
		this.templateFileName = templateFileName;
	}

	/**
	 * @return 内容类型
	 */
	public String getContentType() {
		return contentType;
	}
	/**
	 * 设置内容类型
	 * @param contentType 内容类型
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	//---------------------------------------------------------- Getters and Setters start
	
	@Override
	public void render() {
		final HttpServletResponse response = Response.getServletResponse();
		response.setContentType(contentType);
		VelocityUtil.toWriter(templateFileName, Request.getServletRequest(), response);
	}
	
	@Override
	public String toString() {
		return StrUtil.format("[{}] {}", this.contentType, this.templateFileName);
	}
}

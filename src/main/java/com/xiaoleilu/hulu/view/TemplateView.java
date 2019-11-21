package com.xiaoleilu.hulu.view;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.xiaoleilu.hulu.Request;
import com.xiaoleilu.hulu.Response;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;

/**
 * 模板引擎内容
 * 
 * @author Looly
 * @since 2.0.0
 */
public class TemplateView extends ContentTypeView {

	Template template;

	// ---------------------------------------------------------- Constructor start
	public TemplateView() {
	}

	/**
	 * 构造，默认返回HTML
	 * 
	 * @param text 模板文件名或模板内容
	 */
	public TemplateView(String text) {
		this(text, Response.CONTENT_TYPE_HTML);
	}

	/**
	 * 构造
	 * 
	 * @param text 模板文件名或模板内容
	 * @param contentType 返回内容类型
	 */
	public TemplateView(String text, String contentType) {
		this.text = text;
		this.contentType = contentType;
		final TemplateEngine engine = TemplateUtil.createEngine();
		this.template = engine.getTemplate(text);
	}
	// ---------------------------------------------------------- Constructor end

	@Override
	public void render() {
		final HttpServletResponse response = Response.getServletResponse();
		response.setContentType(this.contentType);
		ServletOutputStream out = null;
		try {
			out = Response.getOutputStream();
			this.template.render(Request.getParamMap(), out);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} finally {
			IoUtil.close(out);
		}
	}
}

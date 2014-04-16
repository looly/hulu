package com.sohu.fortune.hulu.render;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import looly.github.hutool.FileUtil;
import looly.github.hutool.HttpUtil;
import looly.github.hutool.IoUtil;
import looly.github.hutool.StrUtil;
import looly.github.hutool.VelocityUtil;

import com.sohu.fortune.hulu.ActionContext;
import com.sohu.fortune.hulu.HuluSetting;
import com.sohu.fortune.hulu.Request;
import com.sohu.fortune.hulu.exception.RenderException;

/**
 * 生成和处理Action返回结果的类
 * 
 * @author xiaolilu
 * 
 */
public class Render {

	/** Callback 函数名，用于JS跨域请求 */
	public final static String DEFAULT_CALLBACK_NAME = "callback";

	/** 返回内容类型：普通文本 */
	public final static String CONTENT_TYPE_TEXT = "text/plain;Global.charset=" + HuluSetting.charset;
	/** 返回内容类型：HTML */
	public final static String CONTENT_TYPE_HTML = "text/html;Global.charset=" + HuluSetting.charset;
	/** 返回内容类型：XML */
	public final static String CONTENT_TYPE_XML = "text/xml;Global.charset=" + HuluSetting.charset;
	/** 返回内容类型：JAVASCRIPT */
	public final static String CONTENT_TYPE_JAVASCRIPT = "application/javascript;Global.charset=" + HuluSetting.charset;
	/** 返回内容类型：JSON */
	public final static String CONTENT_TYPE_JSON = "application/json;Global.charset=" + HuluSetting.charset;

	/**
	 * 重定向到一个新地址<br>
	 * status 302
	 * 
	 * @param uri 新地址
	 * @throws IOException
	 */
	public static final void redirect(String uri) {
		try {
			ActionContext.getResponse().sendRedirect(uri);
		} catch (IOException e) {
			throw new RenderException("Redirect to [" + uri + "] error!");
		}
	}

	/**
	 * 301跳转
	 * 
	 * @param url
	 * @param isWithParamStr
	 */
	public static void redirect301(String url, boolean isWithParamStr) {

		String contextPath = ActionContext.getContextPath();
		if (contextPath != null && url.indexOf("://") == -1) {
			url = contextPath + url;
		}

		if (isWithParamStr) {
			// 加入请求参数
			String paramStr = ActionContext.getRequest().getQueryString();
			if (paramStr != null) url = url + "?" + paramStr;
		}

		HttpServletResponse response = ActionContext.getResponse();
		response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		response.setHeader("Location", url);
		response.setHeader("Connection", "close");
	}

	/**
	 * 服务端跳转
	 * 
	 * @param uri 跳转的URI
	 * @throws ServletException
	 * @throws IOException
	 */
	public static final void forward(String uri) throws ServletException, IOException {
		HttpServletRequest request = ActionContext.getRequest();
		request.getRequestDispatcher(uri).forward(request, ActionContext.getResponse());
	}

	/**
	 * 返回普通文本
	 * 
	 * @param text
	 */
	public static void renderText(String text) {
		render(text, CONTENT_TYPE_TEXT, ActionContext.getResponse());
	}

	/**
	 * 返回HTML
	 * 
	 * @param html
	 */
	public static void renderHtml(String html) {
		render(html, CONTENT_TYPE_HTML);
	}

	/**
	 * 返回XML
	 * 
	 * @param xml
	 */
	public static void renderXml(String xml) {
		render(xml, CONTENT_TYPE_XML);
	}
	
	/**
	 * 返回JSON
	 * 
	 * @param json
	 */
	public static void renderJson(String json) {
		render(json, CONTENT_TYPE_JSON);
	}
	
	/**
	 * 返回JS<br>
	 * 此方法会自动获取callback参数，并返回以callback参数值命名的函数
	 * @param data 返回的js数据
	 */
	public static void renderJs(String data) {
		renderJs(data, null);
	}

	/**
	 * 返回JS<br>
	 * 此方法会自动获取callback参数，并返回以callback参数值命名的函数
	 * @param data 返回的js数据
	 * @param callbackParamName 回调函数的参数名称
	 */
	public static void renderJs(String data, String callbackParamName) {
		if (callbackParamName == null) {
			callbackParamName = DEFAULT_CALLBACK_NAME;
		}
		if(data == null) {
			data = StrUtil.EMPTY;
		}
		
		final String callback = Request.getParam(callbackParamName, DEFAULT_CALLBACK_NAME);
		render(StrUtil.format("{}({})", callback, data), CONTENT_TYPE_JAVASCRIPT);
	}

	/**
	 * 返回Velocity处理后的HTML内容<br>
	 * 模板的参数全部来自于Request 的Parameter，如需自定义，请填充其值
	 * @param templateFileName 模板文件
	 */
	public static void renderVelocityHtml(String templateFileName) {
		renderVelocity(templateFileName, CONTENT_TYPE_HTML);
	}

	/**
	 * 返回Velocity处理后的内容<br>
	 * 模板的参数全部来自于Request 的Parameter，如需自定义，请填充其值
	 * @param templateFileName 模板文件
	 * @param contentType 文件类型
	 */
	public static void renderVelocity(String templateFileName, String contentType) {
		HttpServletResponse response = ActionContext.getResponse();
		response.setContentType(contentType);
		VelocityUtil.toWriter(templateFileName, ActionContext.getRequest(), response);
	}

	/**
	 * 返回数据给客户端
	 * 
	 * @param text 返回的内容
	 * @param contentType 返回的内容类型
	 */
	public static void render(String text, String contentType) {
		render(text, contentType, ActionContext.getResponse());
	}

	/**
	 * 返回数据给客户端
	 * 
	 * @param text 返回的内容
	 * @param contentType 返回的类型
	 * @param response Response对象
	 */
	public static void render(String text, String contentType, HttpServletResponse response) {
		// response.setCharacterEncoding(CHARSET); //在Filter中提供
		response.setContentType(contentType);

		Writer writer = null;
		try {
			writer = response.getWriter();
			writer.write(text);
			writer.flush();
		} catch (IOException e) {
			throw new RenderException("Error when output to client!", e);
		} finally {
			FileUtil.close(writer);
		}
	}

	/**
	 * 响应文件<br>
	 * 文件过大将下载失败
	 * @param file 文件对象
	 * @param responseFileName 响应给客户端的文件名，如果为空则使用编码后的原文件名
	 * @param bufferSize 缓存大小
	 */
	public static void renderFile(File file, String responseFileName, int bufferSize) {
		long fileLength = file.length();
		if(fileLength > Integer.MAX_VALUE) {
			throw new RenderException("File [" + file.getName() + "] is too large, file size: [" + fileLength + "]");
		}
		
		if (StrUtil.isBlank(responseFileName)) {
			// 如果未指定文件名，使用原文件名
			responseFileName = file.getName();
			HttpUtil.encode(responseFileName, HuluSetting.charset);
		}
		
		HttpServletResponse response = ActionContext.getResponse();
		response.addHeader("Content-disposition", "attachment; filename=" + responseFileName);
		response.setContentType("application/octet-stream");
		response.setContentLength((int)fileLength);
		
		FileInputStream in = null;
		ServletOutputStream out = null;
		try {
			in = new FileInputStream(file);
			out = response.getOutputStream();
			IoUtil.copy(in, out);
		} catch (Exception e) {
			throw new RenderException("Render file error!", e);
		}finally {
			FileUtil.close(in);
			FileUtil.close(out);
		}
	}
	
	/**
	 * 响应到Jsp文件
	 * @param view jsp的url
	 */
	public static void renderJsp(String view) {
		try {
			forward(view);
		} catch (Exception e) {
			throw new RenderException(e.getMessage(), e);
		}
	}
}

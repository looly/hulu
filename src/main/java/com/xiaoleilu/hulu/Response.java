package com.xiaoleilu.hulu;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.xiaoleilu.hulu.exception.RenderException;
import com.xiaoleilu.hulu.view.View;
import com.xiaoleilu.hutool.io.IoUtil;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;

/**
 * 响应静态类
 * @author xiaoleilu
 *
 */
public class Response {
	private static final Log log = LogFactory.get();
	
	/** Callback 函数名，用于JS跨域请求 */
	public final static String DEFAULT_CALLBACK_NAME = "callback";

	/** 返回内容类型：普通文本 */
	public final static String CONTENT_TYPE_TEXT = "text/plain";
	/** 返回内容类型：HTML */
	public final static String CONTENT_TYPE_HTML = "text/html";
	/** 返回内容类型：XML */
	public final static String CONTENT_TYPE_XML = "text/xml";
	/** 返回内容类型：JAVASCRIPT */
	public final static String CONTENT_TYPE_JAVASCRIPT = "application/javascript";
	/** 返回内容类型：JSON */
	public final static String CONTENT_TYPE_JSON = "application/json";
	public final static String CONTENT_TYPE_JSON_IE = "text/json";
	
	/**Servlet Response */
	private final static ThreadLocal<HttpServletResponse> responseThreadLocal = new ThreadLocal<HttpServletResponse>();
	
	private Response() {
	}
	
	/**
	 * @return 获得Servlet Response
	 */
	public static HttpServletResponse getServletResponse() {
		return responseThreadLocal.get();
	}
	
	/**
	 * @return 获得输出流
	 * @throws IOException
	 */
	public static ServletOutputStream getOutputStream() throws IOException{
		return getServletResponse().getOutputStream();
	}
	
	/**
	 * @return 获得PrintWriter
	 * @throws IOException
	 */
	public static PrintWriter getWriter() throws IOException{
		return getServletResponse().getWriter();
	}
	
	/**
	 * 设置响应的Header
	 * @param name 名
	 * @param value 值，可以是String，Date， int
	 */
	public static void setHeader(String name, Object value) {
		final HttpServletResponse response = getServletResponse();
		if(value instanceof String) {
			response.setHeader(name, (String)value);
		}else if(Date.class.isAssignableFrom(value.getClass())) {
			response.setDateHeader(name, ((Date)value).getTime());
		}else if(value instanceof Integer || "int".equals(value.getClass().getSimpleName().toLowerCase())) {
			response.setIntHeader(name, (Integer)value);
		}else {
			response.setHeader(name, value.toString());
		}
	}
	
	/**
	 * 设置响应状态码
	 * @param statusCode 状态码，见{@code javax.servlet.http.HttpServletResponse}
	 */
	public static void setStatus(int statusCode) {
		getServletResponse().setStatus(statusCode);
	}
	
	/**
	 * 设置Content-Type<br>
	 * 如果使用Render将被其返回类型覆盖
	 * @param contentType Content-Type
	 */
	public static void setContentType(String contentType){
		getServletResponse().setContentType(contentType);
	}
	
	/**
	 * 写入View到客户端
	 * @param view View
	 */
	public static void render(View view){
		view.render();
	}
	
	/**
	 * 将错误发送到容器
	 * @param errorCode 错误代码
	 * @param errorContent 错误信息
	 */
	public static void sendError(int errorCode, String errorContent) {
		HttpServletResponse response = Response.getServletResponse();
		try {
			if(HuluSetting.isDevMode) {
				response.sendError(errorCode);
			}else {
				response.sendError(errorCode, errorContent);
			}
		} catch (IOException e) {
			log.error("Error when sendError!", e);
		}
	}
	
	// --------------------------------------------------------- Write start
	/**
	 * 返回数据给客户端
	 * 
	 * @param text 返回的内容
	 * @param contentType 返回的类型
	 */
	public static void write(String text, String contentType) {
		setContentType(contentType);
		Writer writer = null;
		try {
			writer = getWriter();
			writer.write(text);
			writer.flush();
		} catch (IOException e) {
			throw new RenderException("Error when output to client!", e);
		} finally {
			IoUtil.close(writer);
		}
	}
	
	/**
	 * 返回数据给客户端
	 * 
	 * @param in 需要返回客户端的内容
	 * @param contentType 返回的类型
	 */
	public static void write(InputStream in, String contentType) {
		setContentType(contentType);
		write(in);
	}
	
	/**
	 * 返回数据给客户端
	 * 
	 * @param in 需要返回客户端的内容
	 */
	public static void write(InputStream in) {
		ServletOutputStream out = null;
		try {
			out = getOutputStream();
			IoUtil.copy(in, out);
		} catch (IOException e) {
			throw new RenderException("Error when output to client!", e);
		} finally {
			IoUtil.close(out);
			IoUtil.close(in);
		}
	}
	// --------------------------------------------------------- Write end
	
	// --------------------------------------------------------- Cookie start
	/**
	 * 设定返回给客户端的Cookie
	 * 
	 * @param cookie
	 */
	public final static void addCookie(Cookie cookie) {
		getServletResponse().addCookie(cookie);
	}

	/**
	 * 设定返回给客户端的Cookie
	 * 
	 * @param name Cookie名
	 * @param value Cookie值
	 */
	public final static void addCookie(String name, String value) {
		getServletResponse().addCookie(new Cookie(name, value));
	}

	/**
	 * 设定返回给客户端的Cookie
	 * 
	 * @param name cookie名
	 * @param value cookie值
	 * @param maxAgeInSeconds -1: 关闭浏览器清除Cookie. 0: 立即清除Cookie. n>0 : Cookie存在的秒数.
	 * @param path Cookie的有效路径
	 * @param domain the domain name within which this cookie is visible; form is according to RFC 2109
	 */
	public final static void addCookie(String name, String value, int maxAgeInSeconds, String path, String domain) {
		Cookie cookie = new Cookie(name, value);
		if (domain != null) {
			cookie.setDomain(domain);
		}
		cookie.setMaxAge(maxAgeInSeconds);
		cookie.setPath(path);
		addCookie(cookie);
	}

	/**
	 * 设定返回给客户端的Cookie<br>
	 * Path: "/"<br>
	 * No Domain
	 * 
	 * @param name cookie名
	 * @param value cookie值
	 * @param maxAgeInSeconds -1: 关闭浏览器清除Cookie. 0: 立即清除Cookie. n>0 : Cookie存在的秒数.
	 */
	public final static void addCookie(String name, String value, int maxAgeInSeconds) {
		addCookie(name, value, maxAgeInSeconds, "/", null);
	}
	// --------------------------------------------------------- Cookie end
	
	// ------------------------------------------------------------------------------------ Protected method start
	/**
	 * 初始化Response对象
	 * 
	 * @param res 响应对象
	 */
	protected final static void init(HttpServletResponse res) {
		// -- 字符集的过滤
		String charset = HuluSetting.charset;
		try {
			res.setCharacterEncoding(charset);
		} catch (Exception e) {
			log.warn("Charset [{}] not support!", charset);
		}
		responseThreadLocal.set(res);
	}
	// ------------------------------------------------------------------------------------ Protected method end
}

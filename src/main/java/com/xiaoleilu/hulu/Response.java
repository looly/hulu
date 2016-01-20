package com.xiaoleilu.hulu;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;

/**
 * 响应静态类
 * @author xiaoleilu
 *
 */
public class Response {
	private static final Log log = LogFactory.get();
	
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

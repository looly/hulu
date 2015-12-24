package com.xiaoleilu.hulu;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 响应静态类
 * @author xiaoleilu
 *
 */
public class Response {
	
	private Response() {
	}
	
	/**
	 * @return 获得Servlet Response
	 */
	public static HttpServletResponse getServletResponse() {
		return ActionContext.getResponse();
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
	public final static void setHeader(String name, Object value) {
		if(value instanceof String) {
			ActionContext.getResponse().setHeader(name, (String)value);
		}else if(Date.class.isAssignableFrom(value.getClass())) {
			ActionContext.getResponse().setDateHeader(name, ((Date)value).getTime());
		}else if(value instanceof Integer || "int".equals(value.getClass().getSimpleName().toLowerCase())) {
			ActionContext.getResponse().setIntHeader(name, (Integer)value);
		}else {
			ActionContext.getResponse().setHeader(name, value.toString());
		}
	}
	
	/**
	 * 设置响应状态码
	 * @param statusCode 状态码，见{@code javax.servlet.http.HttpServletResponse}
	 */
	public final static void setStatus(int statusCode) {
		ActionContext.getResponse().setStatus(statusCode);
	}
	
	// --------------------------------------------------------- Cookie start
	/**
	 * 设定返回给客户端的Cookie
	 * 
	 * @param cookie
	 */
	public final static void addCookie(Cookie cookie) {
		ActionContext.getResponse().addCookie(cookie);
	}

	/**
	 * 设定返回给客户端的Cookie
	 * 
	 * @param name Cookie名
	 * @param value Cookie值
	 */
	public final static void addCookie(String name, String value) {
		ActionContext.getResponse().addCookie(new Cookie(name, value));
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
}

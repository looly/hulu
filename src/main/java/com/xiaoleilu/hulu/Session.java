package com.xiaoleilu.hulu;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

/**
 * Http会话，基于Servlet
 * @author Looly
 *
 */
public class Session {
	
	/**
	 * 获得HttpSession，当Session未创建时自动创建
	 * @return HttpSession
	 */
	public static HttpSession getHttpSession() {
		return Request.getSession();
	}
	
	/**
	 * 获得HttpSession，当Session未创建时返回null
	 * @return HttpSession
	 */
	public static HttpSession getNullableHttpSession() {
		return Request.getSession(false);
	}
	
	/**
	 * 设置一个Session值
	 * @param name 名
	 * @param value 值
	 */
	public static void set(String name, Object value) {
		getHttpSession().setAttribute(name, value);
	}
	
	/**
	 * 移除一个Session值
	 * @param name 名
	 */
	public static void remove(String name) {
		getHttpSession().removeAttribute(name);
	}
	
	/**
	 * 获得一个Session值
	 * @param name 名
	 * @return 值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(String name) {
		return (T)getHttpSession().getAttribute(name);
	}
	
	/**
	 * 获得所有Session名
	 * @return 名
	 */
	public static Enumeration<String> getNames() {
		return getHttpSession().getAttributeNames();
	}
	
	/**
	 * 获得SessionId，这个ID由Servlet容器生成
	 * @return ID
	 */
	public static String id() {
		return getHttpSession().getId();
	}
	
	/**
	 * @return Session创建时间
	 */
	public static long createTime() {
		return getHttpSession().getCreationTime();
	}
	
	/**
	 * @return 上次访问Session的时间
	 */
	public static long lastAccessedTime() {
		return getHttpSession().getLastAccessedTime();
	}
	
	/**
	 * @return Session有效时长
	 */
	public static long maxInactiveInterval() {
		return getHttpSession().getMaxInactiveInterval();
	}
	
	/**
	 * 设置Session有效时长
	 * @param interval 时长
	 */
	public static void setMaxInactiveInterval(int interval) {
		getHttpSession().setMaxInactiveInterval(interval);
	}
	
	/**
	 * 将Session置为无效并删除所有Session的值
	 */
	public static void invalidate() {
		getHttpSession().invalidate();
	}
	
	/**
	 * 客户端是否拒绝Session
	 * @return 如果为true，说明服务端设置了Session，但是客户端并不支持
	 */
	public static boolean isNew() {
		return getHttpSession().isNew();
	}
}

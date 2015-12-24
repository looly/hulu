package com.xiaoleilu.hulu;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.xiaoleilu.hulu.exception.ActionRuntimeException;
import com.xiaoleilu.hulu.upload.MultipartFormData;
import com.xiaoleilu.hulu.upload.UploadSetting;
import com.xiaoleilu.hutool.CharsetUtil;
import com.xiaoleilu.hutool.Conver;
import com.xiaoleilu.hutool.DateUtil;
import com.xiaoleilu.hutool.InjectUtil;
import com.xiaoleilu.hutool.StrUtil;
import com.xiaoleilu.hutool.http.HttpUtil;

/**
 * 请求静态类
 * 
 * @author xiaoleilu
 */
public class Request {
	
	public static final String METHOD_DELETE = "DELETE";
	public static final String METHOD_HEAD = "HEAD";
	public static final String METHOD_GET = "GET";
	public static final String METHOD_OPTIONS = "OPTIONS";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_TRACE = "TRACE";
	
	
	/** 存放每次请求的参数，由于ActionMethod为单例存在，故在此使用ThreadLocal */
	private static ThreadLocal<String[]> params = new ThreadLocal<String[]>();

	private Request() {
	}

	/**
	 * @return 获得Servlet Request对象
	 */
	public static HttpServletRequest getServletRequest() {
		return ActionContext.getRequest();
	}

	/**
	 * @return 获得Session会话
	 */
	public final static HttpSession getSession() {
		return ActionContext.getRequest().getSession();
	}

	/**
	 * 获得客户端IP<br>
	 * 考虑了Nginx等前端服务器转发和反向代理的情况
	 * 
	 * @return 客户端IP
	 */
	public static String getIp() {
		return HttpUtil.getClientIP(ActionContext.getRequest());
	}

	// --------------------------------------------------------- Header start
	
	/**
	 * 忽略大小写获得请求header中的信息
	 * @param headerKey 头信息的KEY
	 * @return header值
	 */
	public final static String getHeaderIgnoreCase(String headerKey) {
		Enumeration<String> headerNames = ActionContext.getRequest().getHeaderNames();
		String name = null;
		while(headerNames.hasMoreElements()) {
			name = headerNames.nextElement();
			if(name != null && name.equalsIgnoreCase(headerKey)) {
				return getHeader(headerKey);
			}
		}
		
		return null;
	}
	
	/**
	 * 获得请求header中的信息
	 * 
	 * @param headerKey 头信息的KEY
	 * @param charset 字符集
	 * @return header值
	 */
	public final static String getHeader(String headerKey, String charset) {
		final String header = getHeader(headerKey);
		if (null != header) {
			try {
				return new String(header.getBytes(CharsetUtil.ISO_8859_1), charset);
			} catch (UnsupportedEncodingException e) {
				throw new ActionRuntimeException(StrUtil.format("Error charset {} for http request header.", charset));
			}
		}
		return null;
	}

	/**
	 * 使用ISO8859_1字符集获得Header内容<br>
	 * 由于Header中很少有中文，故一般情况下无需转码
	 * 
	 * @param headerKey 头信息的KEY
	 * @return 值
	 */
	public final static String getHeader(String headerKey) {
		return ActionContext.getRequest().getHeader(headerKey);
	}

	// --------------------------------------------------------- Header end

	// --------------------------------------------------------- Cookie start
	/**
	 * 获得指定的Cookie
	 * 
	 * @param name cookie名
	 * @return Cookie对象
	 */
	public final static Cookie getCookie(String name) {
		final Map<String, Cookie> cookieMap = readCookieMap();
		return cookieMap == null ? null : cookieMap.get(name);
	}

	/**
	 * 将cookie封装到Map里面
	 * 
	 * @return Cookie map
	 */
	public final static Map<String, Cookie> readCookieMap() {
		Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
		Cookie[] cookies = ActionContext.getRequest().getCookies();
		if (null == cookies) {
			return null;
		}
		for (Cookie cookie : cookies) {
			cookieMap.put(cookie.getName().toLowerCase(), cookie);
		}
		return cookieMap;
	}

	// --------------------------------------------------------- Cookie end

	// --------------------------------------------------------- Parameter start
	/**
	 * 获得url中的参数，RestFull风格
	 * 
	 * @return url中的参数
	 */
	public static String[] getUrlParams() {
		return params.get();
	}
	
	/**
	 * 获得url中的单个参数，RestFull风格
	 * @param index 获得第几个URL参数的
	 * @return url中的参数
	 */
	public static String getUrlParam(int index) {
		String[] urlParams = getUrlParams();
		if(null != urlParams && urlParams.length > index) {
			return urlParams[index];
		}
		return null;
	}

	/**
	 * 获得参数，只是简单调用Servlet的getParameter方法
	 * @param name 参数名
	 * @return 请求参数
	 */
	public static String getParam(String name) {
		return ActionContext.getRequest().getParameter(name);
	}
	
	/**
	 * 获得GET请求参数<br>
	 * 会根据浏览器类型自动识别GET请求的编码方式从而解码<br>
	 * 考虑到Servlet容器中会首先解码，给定的charsetOfServlet就是Servlet设置的解码charset<br>
	 * charsetOfServlet为null则默认的ISO_8859_1
	 * @param name 参数名
	 * @param charsetOfServlet Servlet容器中的字符集
	 * @return 获得请求参数
	 */
	public static String getParam(String name, String charsetOfServlet) {
		return convertGetMethodParamValue(getParam(name), charsetOfServlet);
	}
	
	/**
	 * @param name 参数名
	 * @param defaultValue 当客户端未传参的默认值
	 * @return 获得String类型请求参数
	 */
	public static String getStrParam(String name, String defaultValue) {
		final String param = convertGetMethodParamValue(getParam(name), null);
		return StrUtil.isBlank(param) ? defaultValue : param;
	}
	
	/**
	 * 获得String类型请求参数
	 * 会根据浏览器类型自动识别GET请求的编码方式从而解码<br>
	 * 考虑到Servlet容器中会首先解码，给定的charsetOfServlet就是Servlet设置的解码charset<br>
	 * @param name 参数名
	 * @param defaultValue 当客户端未传参的默认值
	 * @param charsetOfServlet Servlet的字符集
	 * @return 获得String类型请求参数
	 */
	public static String getStrParam(String name, String defaultValue, String charsetOfServlet) {
		final String param = convertGetMethodParamValue(getParam(name), charsetOfServlet);
		return StrUtil.isBlank(param) ? defaultValue : param;
	}
	
	/**
	 * @param name 参数名
	 * @param defaultValue 当客户端未传参的默认值
	 * @return 获得Integer类型请求参数
	 */
	public static Integer getIntParam(String name, Integer defaultValue) {
		return Conver.toInt(getParam(name), defaultValue);
	}
	
	/**
	 * @param name 参数名
	 * @param defaultValue 当客户端未传参的默认值
	 * @return 获得long类型请求参数
	 */
	public static Long getLongParam(String name, Long defaultValue) {
		return Conver.toLong(getParam(name), defaultValue);
	}
	
	/**
	 * @param name 参数名
	 * @param defaultValue 当客户端未传参的默认值
	 * @return 获得Double类型请求参数
	 */
	public static Double getDoubleParam(String name, Double defaultValue) {
		return Conver.toDouble(getParam(name), defaultValue);
	}
	
	/**
	 * @param name 参数名
	 * @param defaultValue 当客户端未传参的默认值
	 * @return 获得Float类型请求参数
	 */
	public static Float getFloatParam(String name, Float defaultValue) {
		return Conver.toFloat(getParam(name), defaultValue);
	}
	
	/**
	 * @param name 参数名
	 * @param defaultValue 当客户端未传参的默认值
	 * @return 获得Boolean类型请求参数
	 */
	public static Boolean getBoolParam(String name, Boolean defaultValue) {
		return Conver.toBool(getParam(name), defaultValue);
	}
	
	/**
	 * 格式：<br>
	* 1、yyyy-MM-dd HH:mm:ss <br>
	* 2、yyyy-MM-dd <br>
	* 3、HH:mm:ss <br>
	* 
	 * @param name 参数名
	 * @param defaultValue 当客户端未传参的默认值
	 * @return 获得Date类型请求参数，默认格式：
	 */
	public static Date getDateParam(String name, Date defaultValue) {
		String param = getParam(name);
		return StrUtil.isBlank(param) ? defaultValue : DateUtil.parse(param);
	}
	
	/**
	 * @param name 参数名
	 * @param format 格式
	 * @param defaultValue 当客户端未传参的默认值
	 * @return 获得Date类型请求参数
	 */
	public static Date getDateParam(String name, String format, Date defaultValue) {
		String param = getParam(name);
		return StrUtil.isBlank(param) ? defaultValue : DateUtil.parse(param, format);
	}

	/**
	 * 获得请求参数<br>
	 * 数组类型值，常用于表单中的多选框
	 * 
	 * @param name 参数名
	 * @return 数组
	 */
	public static String[] getArrayParam(String name) {
		return ActionContext.getRequest().getParameterValues(name);
	}

	/**
	 * 获得所有请求参数
	 * 
	 * @return Map
	 */
	public static Map<String, String[]> getParams() {
		return ActionContext.getRequest().getParameterMap();
	}
	
	/**
	 * 从Request中获得Vo对象
	 * @param clazz VO类，必须包含默认造方法
	 * @param isWtihModeName 参数是否带Vo类名，i.e true: user.name, false: name
	 * @return value Object
	 */
	public static <T> T getVo(Class<T> clazz, boolean isWtihModeName) {
		T vo = null;
		try {
			vo = clazz.newInstance();
		} catch (Exception e) {
			throw new ActionRuntimeException(StrUtil.format("Can not instance value object [{}]", clazz.getName()), e);
		}
		return fillVo(vo, isWtihModeName);
	}
	
	/**
	 * 填充 Value Object对象
	 * @param vo Value Object对象
	 * @param isWtihModeName 参数是否带Vo类名，i.e true: user.name, false: name
	 * @return vo
	 */
	public static <T> T fillVo(T vo, boolean isWtihModeName) {
		InjectUtil.injectFromRequest(vo, getServletRequest(), isWtihModeName);
		return vo;
	}

	/**
	 * 获得MultiPart表单内容，多用于获得上传的文件
	 * 在同一次请求中，此方法只能被执行一次！
	 * @return MultipartFormData
	 * @throws IOException
	 */
	public static MultipartFormData getMultipartFormData() throws IOException {
		return getMultipartFormData(null);
	}
	
	/**
	 * 获得MultiPart表单内容，多用于获得上传的文件
	 * @param paramName 文件对应的参数名
	 * @return 文件流
	 * @throws IOException
	 */
	/**
	 * 获得multipart/form-data 表单内容<br>
	 * 包括文件和普通表单数据<br>
	 * 在同一次请求中，此方法只能被执行一次！
	 * @param uploadSetting 上传文件的设定，包括最大文件大小、保存在内存的边界大小、临时目录、扩展名限定等
	 * @return MultiPart表单
	 * @throws IOException
	 */
	public static MultipartFormData getMultipartFormData(UploadSetting uploadSetting) throws IOException {
		MultipartFormData formData = new MultipartFormData(uploadSetting);
		formData.parseRequest(ActionContext.getRequest());

		return formData;
	}
	
	/**
	 * 设置Request的Attribute，用于在同一个会话中传递参数
	 * @param name 名
	 * @param value 值
	 */
	public static void setAttr(String name, Object value) {
		ActionContext.getRequest().setAttribute(name, value);
	}
	
	/**
	 * 设置Request的Attribute，用于在同一个会话中传递参数
	 * @param values 键值对
	 */
	public static void setAttr(Map<String, Object> values) {
		for (Entry<String, Object> entry : values.entrySet()) {
			ActionContext.getRequest().setAttribute(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * 获得Request的Attribute，用于在同一个会话中传递参数
	 * @param name 名
	 */
	public static Object getAttr(String name) {
		return ActionContext.getRequest().getAttribute(name);
	}
	
	/**
	 * @return 客户浏览器是否为IE
	 */
	public static boolean isIE() {
		String userAgent = Request.getHeaderIgnoreCase("User-Agent");
		if(StrUtil.isNotBlank(userAgent)) {
			userAgent = userAgent.toUpperCase();
			if(userAgent.contains("MSIE") || userAgent.contains("TRIDENT")) {
				return true;
			}
		}
		return false;
	}
	// --------------------------------------------------------- Parameter end

	// ------------------------------------------------------------------------------------ Protected method start
	/**
	 * 切分并设置url参数<br>
	 * 分隔符定义在HuluSetting中
	 * 
	 * @param urlParam url参数
	 */
	protected static void splitAndSetParams(String urlParam) {
		String[] urlParams = StrUtil.split(urlParam, HuluSetting.urlParamSeparator);
		params.set(urlParams);
	}
	// ------------------------------------------------------------------------------------ Protected method end
	
	// ------------------------------------------------------------------------------------ Private method start
	/**
	 * @return 是否为Get请求
	 */
	private static boolean isGetMethod(){
		return METHOD_GET.equalsIgnoreCase(ActionContext.getRequest().getMethod());
	}
	
	/**
	 * 转换值得编码
	 * 会根据浏览器类型自动识别GET请求的编码方式从而解码<br>
	 * 考虑到Servlet容器中会首先解码，给定的charsetOfServlet就是Servlet设置的解码charset<br>
	 * charsetOfServlet为null则默认的ISO_8859_1
	 * @param value 值
	 * @param charsetOfServlet Servlet的编码
	 * @return 转换后的字符串
	 */
	private static String convertGetMethodParamValue(String value, String charsetOfServlet){
		if(isGetMethod()) {
			if(null == charsetOfServlet) {
				charsetOfServlet = CharsetUtil.ISO_8859_1;
			}
			
			String destCharset = CharsetUtil.UTF_8;
			if(isIE()) {
				//IE浏览器GET请求使用GBK编码
				destCharset = CharsetUtil.GBK;
			}
			
			value = CharsetUtil.convert(value, charsetOfServlet, destCharset);
		}
		return value;
	}
	// ------------------------------------------------------------------------------------ Private method end
}

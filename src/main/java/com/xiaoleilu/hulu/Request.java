package com.xiaoleilu.hulu;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.xiaoleilu.hulu.exception.ActionRuntimeException;
import com.xiaoleilu.hulu.multipart.MultipartFormData;
import com.xiaoleilu.hulu.multipart.UploadFile;
import com.xiaoleilu.hulu.multipart.UploadSetting;
import com.xiaoleilu.hutool.http.HttpUtil;
import com.xiaoleilu.hutool.io.IoUtil;
import com.xiaoleilu.hutool.json.JSON;
import com.xiaoleilu.hutool.json.JSONArray;
import com.xiaoleilu.hutool.json.JSONObject;
import com.xiaoleilu.hutool.json.JSONUtil;
import com.xiaoleilu.hutool.lang.Convert;
import com.xiaoleilu.hutool.lang.Dict;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import com.xiaoleilu.hutool.util.BeanUtil;
import com.xiaoleilu.hutool.util.BeanUtil.ValueProvider;
import com.xiaoleilu.hutool.util.CharsetUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import com.xiaoleilu.hutool.util.DateUtil;
import com.xiaoleilu.hutool.util.StrUtil;

/**
 * 请求静态类
 * 
 * @author xiaoleilu
 */
public class Request {
	private static final Log log = LogFactory.get();

	public static final String METHOD_DELETE = "DELETE";
	public static final String METHOD_HEAD = "HEAD";
	public static final String METHOD_GET = "GET";
	public static final String METHOD_OPTIONS = "OPTIONS";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_TRACE = "TRACE";

	/** Request ThreadLocal*/
	private final static ThreadLocal<HttpServletRequest> servletRequestLocal = new ThreadLocal<>();
	/** 存放每次请求的参数，由于ActionMethod为单例存在，故在此使用ThreadLocal */
	private static ThreadLocal<String[]> urlParamsLocal = new ThreadLocal<>();
	/** 存放Multipart Form Data ThreadLocal*/
	private static ThreadLocal<MultipartFormData> multipartFormDataLocal = new ThreadLocal<>();

	private Request() {
	}

	/**
	 * @return 获得Servlet Request对象
	 */
	public static HttpServletRequest getServletRequest() {
		return servletRequestLocal.get();
	}
	
	/**
	 * @return 获得Path
	 */
	public static String getContextPath() {
		return ActionContext.getContextPath();
	}
	
	/**
	 * 获得Path，获得的Path是去掉项目名的（contextPath）<br>
	 * 返回结果永远以"/"开头，不以"/"结尾
	 * @return 获得Path，获得的Path是去掉
	 */
	public static String getPath() {
		return getPath(getServletRequest());
	}
	
	/**
	 * 获得Path，获得的Path是去掉项目名的（contextPath）<br>
	 * 返回结果永远以"/"开头，不以"/"结尾
	 * 
	 * @param req HttpServletRequest
	 * @return 获得Path，获得的Path是去掉
	 */
	public static String getPath(HttpServletRequest req) {
		return getWellFormPath(req.getRequestURI());
	}

	/**
	 * @return 获得Session会话
	 */
	public final static HttpSession getSession() {
		return getServletRequest().getSession();
	}
	
	/**
	 * @return 获得Session会话
	 * @param isCreate Session不存在的情况下是否创建之
	 */
	public final static HttpSession getSession(boolean isCreate) {
		return getServletRequest().getSession(isCreate);
	}

	/**
	 * 获得客户端IP<br>
	 * 考虑了Nginx等前端服务器转发和反向代理的情况
	 * 
	 * @return 客户端IP
	 */
	public static String getIp() {
		return HttpUtil.getClientIP(getServletRequest());
	}
	
	/**
	 * @return 获得Http Method
	 */
	public final static String getMethod() {
		return getServletRequest().getMethod();
	}
	
	// --------------------------------------------------------- Header start

	/**
	 * 忽略大小写获得请求header中的信息
	 * 
	 * @param headerKey 头信息的KEY
	 * @return header值
	 */
	public final static String getHeaderIgnoreCase(String headerKey) {
		Enumeration<String> headerNames = getServletRequest().getHeaderNames();
		String name = null;
		while (headerNames.hasMoreElements()) {
			name = headerNames.nextElement();
			if (name != null && name.equalsIgnoreCase(headerKey)) {
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
		return getServletRequest().getHeader(headerKey);
	}
	
	/**
	 * @return 是否为GET请求
	 */
	public static boolean isGetMethod() {
		return METHOD_GET.equalsIgnoreCase(getMethod());
	}
	
	/**
	 * @return 是否为POST请求
	 */
	public static boolean isPostMethod() {
		return METHOD_POST.equalsIgnoreCase(getMethod());
	}
	
	/**
	 * @return 客户浏览器是否为IE
	 */
	public static boolean isIE() {
		String userAgent = Request.getHeaderIgnoreCase("User-Agent");
		if (StrUtil.isNotBlank(userAgent)) {
			userAgent = userAgent.toUpperCase();
			if (userAgent.contains("MSIE") || userAgent.contains("TRIDENT")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return 是否为Multipart类型表单，此类型表单用于文件上传
	 */
	public static boolean isMultipart() {
		if (false == isPostMethod()) {
			return false;
		}

		String contentType = getServletRequest().getContentType();
		if (StrUtil.isBlank(contentType)) {
			return false;
		}
		if (contentType.toLowerCase().startsWith("multipart/")) {
			return true;
		}

		return false;
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
		Cookie[] cookies = getServletRequest().getCookies();
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
	 * @return MultipartForm数据，如果非Multipart表单，返回<code>null</code>
	 */
	public static MultipartFormData getMultipart(){
		return multipartFormDataLocal.get();
	}
	
	/**
	 * 获得url中的参数，RestFull风格
	 * 
	 * @return url中的参数
	 */
	public static String[] getUrlParams() {
		return urlParamsLocal.get();
	}

	/**
	 * 获得url中的单个参数，RestFull风格
	 * 
	 * @param index 获得第几个URL参数的
	 * @return url中的参数
	 */
	public static String getUrlParam(int index) {
		String[] urlParams = getUrlParams();
		if (null != urlParams && urlParams.length > index) {
			return urlParams[index];
		}
		return null;
	}

	/**
	 * 获得参数，只是简单调用Servlet的getParameter方法
	 * 
	 * @param name 参数名
	 * @return 请求参数
	 */
	public static String getParam(String name) {
		String param = getServletRequest().getParameter(name);
		if(null == param){
			MultipartFormData multipart = getMultipart();
			if(null != multipart){
				param = multipart.getParam(param);
			}
		}
		return param;
	}

	/**
	 * 获得GET请求参数<br>
	 * 会根据浏览器类型自动识别GET请求的编码方式从而解码<br>
	 * 考虑到Servlet容器中会首先解码，给定的charsetOfServlet就是Servlet设置的解码charset<br>
	 * charsetOfServlet为null则默认的ISO_8859_1
	 * 
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
	 * 获得String类型请求参数 会根据浏览器类型自动识别GET请求的编码方式从而解码<br>
	 * 考虑到Servlet容器中会首先解码，给定的charsetOfServlet就是Servlet设置的解码charset<br>
	 * 
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
	 * @return 获得Number类型请求参数
	 */
	public static Number getNumberParam(String name, Number defaultValue) {
		return Convert.toNumber(getParam(name), defaultValue);
	}

	/**
	 * @param name 参数名
	 * @param defaultValue 当客户端未传参的默认值
	 * @return 获得Integer类型请求参数
	 */
	public static Integer getIntParam(String name, Integer defaultValue) {
		return Convert.toInt(getParam(name), defaultValue);
	}

	/**
	 * @param name 参数名
	 * @param defaultValue 当客户端未传参的默认值
	 * @return 获得long类型请求参数
	 */
	public static Long getLongParam(String name, Long defaultValue) {
		return Convert.toLong(getParam(name), defaultValue);
	}

	/**
	 * @param name 参数名
	 * @param defaultValue 当客户端未传参的默认值
	 * @return 获得Double类型请求参数
	 */
	public static Double getDoubleParam(String name, Double defaultValue) {
		return Convert.toDouble(getParam(name), defaultValue);
	}

	/**
	 * @param name 参数名
	 * @param defaultValue 当客户端未传参的默认值
	 * @return 获得Float类型请求参数
	 */
	public static Float getFloatParam(String name, Float defaultValue) {
		return Convert.toFloat(getParam(name), defaultValue);
	}

	/**
	 * @param name 参数名
	 * @param defaultValue 当客户端未传参的默认值
	 * @return 获得Boolean类型请求参数
	 */
	public static Boolean getBoolParam(String name, Boolean defaultValue) {
		return Convert.toBool(getParam(name), defaultValue);
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
		String[] values = getServletRequest().getParameterValues(name);
		if(null == values){
			final MultipartFormData multipart = getMultipart();
			if(null != multipart){
				values = multipart.getArrayParam(name);
			}
		}
		return values;
	}
	
	/**
	 * 获得上传的文件
	 * @param name 参数名
	 * @return 上传的文件
	 */
	public static UploadFile getFileParam(String name){
		MultipartFormData multipart = getMultipart();
		if(null != multipart){
			return multipart.getFile(name);
		}
		return null;
	}

	/**
	 * 获得所有请求参数
	 * 
	 * @return Map
	 */
	public static Map<String, String[]> getParams() {
		Map<String, String[]> map = getServletRequest().getParameterMap();
		if(CollectionUtil.isEmpty(map)){
			final MultipartFormData multipart = getMultipart();
			if(null != multipart){
				map = multipart.getParamMap();
			}
		}
		return Collections.unmodifiableMap(map);
	}
	
	/**
	 * 获得所有请求参数
	 * 
	 * @return Map
	 */
	public static Map<String, String> getParamMap() {
		Map<String,String> params = new HashMap<String,String>();
		for (Map.Entry<String,String[]> entry : getParams().entrySet()) {
			params.put( entry.getKey(), CollectionUtil.join(entry.getValue(), StrUtil.COMMA) );
		}
		return params;
	}

	/**
	 * 从Request中获得Bean对象
	 * 
	 * @param clazz Bean类，必须包含默认造方法
	 * @return value Object
	 */
	public static <T> T getBean(Class<T> clazz) {
		final T bean = BeanUtil.requestParamToBean(getServletRequest(), clazz);
		
		//注入MultipartFormData 中的参数
		final MultipartFormData multipart = getMultipart();
		if(null != multipart){
			final String beanName = StrUtil.lowerFirst(bean.getClass().getSimpleName());
			BeanUtil.fillBean(bean, new ValueProvider(){
				@Override
				public Object value(String name) {
					String value = multipart.getParam(name);
					if (StrUtil.isEmpty(value)) {
						//使用类名前缀尝试查找值
						value = multipart.getParam(beanName + StrUtil.DOT + name);
						if(StrUtil.isEmpty(value)){
							//此处取得的值为空时跳过，包括null和""
							value = null;
						}
					}
					return value;
				}
			});
		}
		
		return bean;
	}
	
	/**
	 * 将参数填充至Dict
	 * @param dict Dict对象
	 * @return Dict
	 */
	public static <T extends Dict> T fill(T dict){
		dict.putAll(getParamMap());
		return dict;
	}
	// --------------------------------------------------------- Parameter end
	
	// --------------------------------------------------------- Body start
	/**
	 * @return 获得请求体
	 */
	public static String getBody(){
		try {
			return IoUtil.read(getServletRequest().getReader());
		} catch (IOException e) {
			throw new ActionRuntimeException(e);
		}
	}
	
	/**
	 * @return 获得JSON请求体
	 */
	public static JSON getJSONBody(){
		return JSONUtil.parse(getBody());
	}
	
	/**
	 * @return 获得JSONObject请求体
	 */
	public static JSONObject getJSONObjectBody(){
		return JSONUtil.parseObj(getBody());
	}
	
	/**
	 * @return 获得JSONArray请求体
	 */
	public static JSONArray getJSONArrayBody(){
		return JSONUtil.parseArray(getBody());
	}
	// --------------------------------------------------------- Body end
	
	// --------------------------------------------------------- Attribute start
	/**
	 * 设置Request的Attribute，用于在同一个会话中传递参数
	 * 
	 * @param name 名
	 * @param value 值
	 */
	public static void setAttr(String name, Object value) {
		getServletRequest().setAttribute(name, value);
	}

	/**
	 * 设置Request的Attribute，用于在同一个会话中传递参数
	 * 
	 * @param values 键值对
	 */
	public static void setAttr(Map<String, Object> values) {
		for (Entry<String, Object> entry : values.entrySet()) {
			getServletRequest().setAttribute(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * 获得Request的Attribute，用于在同一个会话中传递参数
	 * 
	 * @param name 名
	 */
	public static Object getAttr(String name) {
		return getServletRequest().getAttribute(name);
	}
	// --------------------------------------------------------- Attribute end
	
	// ------------------------------------------------------------------------------------ Protected method start
	/**
	 * 初始化Request对象
	 * 
	 * @param req 请求对象
	 */
	protected final static void init(HttpServletRequest req) {
		// -- 字符集的过滤
		String charset = HuluSetting.charset;
		try {
			req.setCharacterEncoding(charset);
		} catch (Exception e) {
			log.warn("Charset [{}] not support!", charset);
		}
		servletRequestLocal.set(req);
		
		if(isMultipart()){
			try {
				multipartFormDataLocal.set(parseMultipart());
			} catch (IOException e) {
				throw new ActionRuntimeException(e);
			}
		}
	}

	/**
	 * 切分并设置url参数<br>
	 * 分隔符定义在HuluSetting中
	 * 
	 * @param urlParam url参数
	 */
	protected static void splitAndSetParams(String urlParam) {
		String[] urlParams = StrUtil.split(urlParam, HuluSetting.urlParamSeparator);
		urlParamsLocal.set(urlParams);
	}
	// ------------------------------------------------------------------------------------ Protected method end

	// ------------------------------------------------------------------------------------ Private method start
	/**
	 * 转换值得编码 会根据浏览器类型自动识别GET请求的编码方式从而解码<br>
	 * 考虑到Servlet容器中会首先解码，给定的charsetOfServlet就是Servlet设置的解码charset<br>
	 * charsetOfServlet为null则默认的ISO_8859_1
	 * 
	 * @param value 值
	 * @param charsetOfServlet Servlet的编码
	 * @return 转换后的字符串
	 */
	private static String convertGetMethodParamValue(String value, String charsetOfServlet) {
		if (isGetMethod()) {
			if (null == charsetOfServlet) {
				charsetOfServlet = CharsetUtil.ISO_8859_1;
			}

			String destCharset = CharsetUtil.UTF_8;
			if (isIE()) {
				// IE浏览器GET请求使用GBK编码
				destCharset = CharsetUtil.GBK;
			}

			value = CharsetUtil.convert(value, charsetOfServlet, destCharset);
		}
		return value;
	}
	
	/**
	 * 获得MultiPart表单内容，多用于获得上传的文件 在同一次请求中，此方法只能被执行一次！
	 * 
	 * @return MultipartFormData
	 * @throws IOException
	 */
	private static MultipartFormData parseMultipart() throws IOException {
		return parseMultipart(null);
	}

	/**
	 * 获得MultiPart表单内容，多用于获得上传的文件
	 * 
	 * @param paramName 文件对应的参数名
	 * @return 文件流
	 * @throws IOException
	 */
	/**
	 * 获得multipart/form-data 表单内容<br>
	 * 包括文件和普通表单数据<br>
	 * 在同一次请求中，此方法只能被执行一次！
	 * 
	 * @param uploadSetting 上传文件的设定，包括最大文件大小、保存在内存的边界大小、临时目录、扩展名限定等
	 * @return MultiPart表单
	 * @throws IOException
	 */
	private static MultipartFormData parseMultipart(UploadSetting uploadSetting) throws IOException {
		MultipartFormData formData = new MultipartFormData(uploadSetting);
		formData.parseRequest(getServletRequest());

		return formData;
	}
	
	/**
	 * 处理将请求路径标准化为框架目标路径<br>
	 * 1、去除容器的contextPath<br>
	 * 2、去除尾部 "/"<br>
	 * 
	 * @param path 请求Path
	 * @return 框架目标路径
	 */
	private static String getWellFormPath(String path) {
		if(null != path){
			//去掉项目名部分，不去开头的"/"
			final String contextPath = ActionContext.getContextPath();
			if(null != contextPath && false == StrUtil.SLASH.equals(contextPath)){
				path = StrUtil.removePrefix(path, contextPath);
			}
			
			//去掉尾部"/"，如果path为"/"不处理
			if(path.length() > 1) {
				path = StrUtil.removeSuffix(path, StrUtil.SLASH);
			}
		}
		return path;
	}
	// ------------------------------------------------------------------------------------ Private method end
}

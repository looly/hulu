package com.xiaoleilu.hulu.view;

import com.xiaoleilu.hulu.HuluSetting;
import com.xiaoleilu.hulu.Request;
import com.xiaoleilu.hulu.Response;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;

/**
 * 错误视图
 * 
 * @author Looly
 *
 */
public class ErrorView implements View {
	private final static String TEMPLATE_ERROR = "<!DOCTYPE html><html><head><title>Hulu framework - Error report</title><style>h1,h3 {color:white; background-color: gray;}</style></head><body><h1>HTTP Status {} - {}</h1>{}<hr size=\"1\" noshade=\"noshade\" /><p>{}</p><hr size=\"1\" noshade=\"noshade\" /><h3>Hulu framework</h3></body></html>";

	private int errorCode;
	private String errorContent;
	private Throwable e;

	// ---------------------------------------------------------- Constructor start
	/**
	 * 构造
	 */
	public ErrorView() {
	}

	/**
	 * 构造
	 * 
	 * @param errorCode 错误代码
	 * @param errorContent 错误内容
	 */
	public ErrorView(int errorCode, String errorContent) {
		this(errorCode, null, errorContent);
	}
	
	/**
	 * 构造
	 * 
	 * @param errorCode 错误代码
	 * @param e 异常对象
	 */
	public ErrorView(int errorCode, Throwable e) {
		this(errorCode, e, null);
	}
	
	/**
	 * 构造
	 * @param errorCode 错误代码
	 * @param e 异常
	 * @param errorContent 异常内容
	 */
	public ErrorView(int errorCode, Throwable e, String errorContent) {
		this.e = e;
		this.errorCode = errorCode;
		this.errorContent = errorContent;
	}
	// ---------------------------------------------------------- Constructor end

	// ---------------------------------------------------------- Getters and Setters start
	/**
	 * @return 错误代码
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * 设置错误代码
	 * 
	 * @param errorCode 错误代码
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return 错误内容
	 */
	public String getErrorContent() {
		return errorContent;
	}

	/**
	 * 设置错误内容
	 * 
	 * @param errorContent 错误内容
	 */
	public void setErrorContent(String errorContent) {
		this.errorContent = errorContent;
	}

	/**
	 * @return 异常对象
	 */
	public Throwable getE() {
		return e;
	}

	/**
	 * 设置异常对象
	 * 
	 * @param e 异常对象
	 */
	public void setE(Throwable e) {
		this.e = e;
	}
	// ---------------------------------------------------------- Getters and Setters end

	@Override
	public void render() {
		if (false == HuluSetting.isDevMode) {
			Response.sendError(this.errorCode, this.errorContent);
			return;
		}

		if (null != e) {
			StaticLog.error(e);
			if (null == this.errorContent) {
				this.errorContent = StrUtil.EMPTY;
			}
			String stacktraceContent = ExceptionUtil.stacktraceToString(e)
					.replace("\tat", "&nbsp;&nbsp;&nbsp;&nbsp;\tat")
					.replace("\n", "<br/>\n");
			this.errorContent = StrUtil.format(TEMPLATE_ERROR, this.getErrorCode(), Request.getServletRequest().getRequestURI(), this.errorContent, stacktraceContent);
		}
		
		Response.setStatus(errorCode);
		Response.write(errorContent, Response.CONTENT_TYPE_HTML);
	}

	@Override
	public String toString() {
		return JSONUtil.toJsonStr(this);
	}
}

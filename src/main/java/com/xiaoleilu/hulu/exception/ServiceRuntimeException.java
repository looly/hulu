package com.xiaoleilu.hulu.exception;

import cn.hutool.core.util.StrUtil;

/**
 * Action Runtime 异常
 * @author xiaoleilu
 *
 */
public class ServiceRuntimeException extends RuntimeException{
	private static final long serialVersionUID = -8463842111292703724L;

	public ServiceRuntimeException() {
	}
	
	public ServiceRuntimeException(String msg) {
		super(msg);
	}
	
	public ServiceRuntimeException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}
	
	public ServiceRuntimeException(Throwable throwable) {
		super(throwable);
	}
	
	public ServiceRuntimeException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
	
}

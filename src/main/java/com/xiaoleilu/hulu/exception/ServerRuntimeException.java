package com.xiaoleilu.hulu.exception;

import cn.hutool.core.util.StrUtil;

/**
 * 应用服务器异常
 * @author xiaoleilu
 *
 */
public class ServerRuntimeException extends RuntimeException{
	private static final long serialVersionUID = 8068777993829184847L;

	public ServerRuntimeException() {
	}
	
	public ServerRuntimeException(String msg) {
		super(msg);
	}
	
	public ServerRuntimeException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}
	
	public ServerRuntimeException(Throwable throwable) {
		super(throwable);
	}
	
	public ServerRuntimeException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
	
}

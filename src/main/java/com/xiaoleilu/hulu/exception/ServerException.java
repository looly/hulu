package com.xiaoleilu.hulu.exception;

import cn.hutool.core.util.StrUtil;

/**
 * 应用服务器异常
 * @author xiaoleilu
 *
 */
public class ServerException extends Exception{
	private static final long serialVersionUID = 8068777993829184847L;

	public ServerException() {
	}
	
	public ServerException(String msg) {
		super(msg);
	}
	
	public ServerException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}
	
	public ServerException(Throwable throwable) {
		super(throwable);
	}
	
	public ServerException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
	
}

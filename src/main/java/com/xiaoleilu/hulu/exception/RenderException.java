package com.xiaoleilu.hulu.exception;

import cn.hutool.core.util.StrUtil;

/**
 * Render异常
 * @author xiaoleilu
 *
 */
public class RenderException extends RuntimeException{
	private static final long serialVersionUID = 8908884924889746211L;

	public RenderException() {
	}
	
	public RenderException(String msg) {
		super(msg);
	}
	
	public RenderException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}
	
	public RenderException(Throwable throwable) {
		super(throwable);
	}
	
	public RenderException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
	
}

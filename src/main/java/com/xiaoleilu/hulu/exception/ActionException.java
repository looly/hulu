package com.xiaoleilu.hulu.exception;

import com.xiaoleilu.hutool.util.StrUtil;

/**
 * Action异常
 * @author xiaoleilu
 *
 */
public class ActionException extends Exception{
	private static final long serialVersionUID = 8908884924889746211L;

	public ActionException() {
	}
	
	public ActionException(String msg) {
		super(msg);
	}

	public ActionException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}
	
	public ActionException(Throwable throwable) {
		super(throwable);
	}
	
	public ActionException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
	
}

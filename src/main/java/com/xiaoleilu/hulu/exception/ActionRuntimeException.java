package com.xiaoleilu.hulu.exception;

/**
 * Action Runtime 异常
 * @author xiaoleilu
 *
 */
public class ActionRuntimeException extends RuntimeException{
	private static final long serialVersionUID = 5389652593979910541L;

	public ActionRuntimeException() {
	}
	
	public ActionRuntimeException(String msg) {
		super(msg);
	}
	
	public ActionRuntimeException(Throwable throwable) {
		super(throwable);
	}
	
	public ActionRuntimeException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
	
}

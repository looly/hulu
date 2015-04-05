package com.xiaoleilu.hulu.exception;

/**
 * Action Runtime 异常
 * @author xiaoleilu
 *
 */
public class DaoRuntimeException extends RuntimeException{
	private static final long serialVersionUID = 5389652593979910541L;

	public DaoRuntimeException() {
	}
	
	public DaoRuntimeException(String msg) {
		super(msg);
	}
	
	public DaoRuntimeException(Throwable throwable) {
		super(throwable);
	}
	
	public DaoRuntimeException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
	
}

package com.sohu.fortune.hulu.exception;

/**
 * 业务层异常
 * @author xiaoleilu
 *
 */
public class ServiceException extends Exception{
	private static final long serialVersionUID = 6919660122933117668L;

	public ServiceException() {
	}
	
	public ServiceException(String msg) {
		super(msg);
	}
	
	public ServiceException(Throwable throwable) {
		super(throwable);
	}
	
	public ServiceException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
	
}

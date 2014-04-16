package com.sohu.fortune.hulu.exception;

/**
 * DAO异常
 * @author xiaoleilu
 *
 */
public class DaoException extends Exception{
	private static final long serialVersionUID = 5610404179494414693L;

	public DaoException() {
	}
	
	public DaoException(String msg) {
		super(msg);
	}
	
	public DaoException(Throwable throwable) {
		super(throwable);
	}
	
	public DaoException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
	
}

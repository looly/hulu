package com.xiaoleilu.hulu.interceptor;

import com.xiaoleilu.hulu.ActionMethod;
import com.xiaoleilu.hulu.exception.ActionException;

/**
 * 拦截器<br>
 * 拦截器都是单例存在的！
 * @author xiaoleilu
 *
 */
public interface Interceptor {
	
	/**
	 * 之前执行的方法<br>
	 * 返回false表示中断
	 * @param actionMethod ActionMethod
	 * @throws ActionException Action异常
	 */
	void invoke(ActionMethod actionMethod) throws ActionException;
}

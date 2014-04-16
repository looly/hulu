package com.sohu.fortune.hulu.interceptor;

import com.sohu.fortune.hulu.ActionMethod;
import com.sohu.fortune.hulu.exception.ActionException;

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
	 * @return 是否继续执行
	 * @throws ActionException 
	 */
	public void invoke(ActionMethod actionMethod) throws ActionException;
}

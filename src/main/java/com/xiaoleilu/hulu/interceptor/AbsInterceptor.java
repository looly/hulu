package com.xiaoleilu.hulu.interceptor;

import com.xiaoleilu.hulu.ActionMethod;
import com.xiaoleilu.hulu.exception.ActionException;

/**
 * 
 * @author Looly
 *
 */
public abstract class AbsInterceptor implements Interceptor{

	@Override
	public void invoke(ActionMethod actionMethod) throws ActionException {
		if(before(actionMethod)) {
			actionMethod.invoke();
			after(actionMethod);
		}
	}

	/**
	 * 方法或者下一个过滤器执行前执行的方法
	 * @param actionMethod Action方法
	 * @return
	 */
	public abstract boolean before(ActionMethod actionMethod);
	
	/**
	 * 方法或者下一个过滤器执结束执行的方法
	 * @param actionMethod Action方法
	 */
	public abstract void after(ActionMethod actionMethod);
}

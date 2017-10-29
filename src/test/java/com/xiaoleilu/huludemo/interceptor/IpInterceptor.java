package com.xiaoleilu.huludemo.interceptor;

import com.xiaoleilu.hulu.ActionMethod;
import com.xiaoleilu.hulu.Request;
import com.xiaoleilu.hulu.exception.ActionException;
import com.xiaoleilu.hulu.interceptor.Interceptor;
import com.xiaoleilu.hutool.log.StaticLog;

/**
 * 请求控制过滤器<br>
 * 1、检查访问频率
 * 
 * @author Looly
 *
 */
public class IpInterceptor implements Interceptor {

	@Override
	public void invoke(ActionMethod actionMethod) throws ActionException {
		String ip = Request.getIp();
		StaticLog.debug("IP: {}", ip);
		actionMethod.invoke();
	}

}

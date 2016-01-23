package com.xiaoleilu.hulu;

import com.xiaoleilu.hulu.exception.ActionException;
import com.xiaoleilu.hulu.render.ErrorRender;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.StaticLog;

/**
 * Action处理类
 * 
 * @author xiaoleilu
 */
public class ActionHandler {
	private static Log log = StaticLog.get();
	
	private ActionMapping actionMapping;

	// -------------------------------------------------------------------- Constructor start
	public ActionHandler(String[] packageNames) {
		actionMapping = new ActionMapping(packageNames);
	}
	// -------------------------------------------------------------------- Constructor end
	
	/**
	 * 处理请求
	 * 
	 * @return 是否继续执行后续步骤
	 */
	public final boolean handle() {
		return handle(Request.getPath());
	}

	/**
	 * 处理请求
	 * 
	 * @param target 请求目标（请求路径）
	 * @return 是否继续执行后续步骤
	 */
	public final boolean handle(String target) {
		if (HuluSetting.isDevMode) {
			log.debug("Client [{}] {} [{}]", Request.getIp(), Request.getMethod(), target);
		}

		final ActionMethod actionMethod = actionMapping.getActionMethod(target);
		if (actionMethod == null || actionMethod.isHttpMethodMatch() == false) {
			//无对应的Action或者Http方法不匹配，跳过执行后续过程
			return true;
		}

		//重置过滤器执行游标，从第一个过滤器开始执行
		actionMethod.resetInterceptorPosition();
		try {
			actionMethod.invoke();
		} catch (ActionException e) {
			ErrorRender.render500(e.getCause());
		}

		return false;
	}
}

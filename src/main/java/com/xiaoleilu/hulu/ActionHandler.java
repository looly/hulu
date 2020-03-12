package com.xiaoleilu.hulu;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.log.Log;
import com.xiaoleilu.hulu.exception.ActionException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Action处理类
 * 
 * @author xiaoleilu
 */
public class ActionHandler{
	private static Log log = Log.get();
	
	private ActionMapping actionMapping;

	// -------------------------------------------------------------------- Constructor start
	public ActionHandler(String[] packageNames) {
		actionMapping = new ActionMapping(packageNames);
	}
	// -------------------------------------------------------------------- Constructor end
	
	/**
	 * 处理请求
	 * 
	 * @param req ServletRequest
	 * @param res ServletResponse
	 * @return 是否处理成功
	 */
	public final boolean handle(HttpServletRequest req, HttpServletResponse res) {
		return handle(req, res, Request.getPath(req));
	}

	/**
	 * 处理请求
	 * 
	 * @param req ServletRequest
	 * @param res ServletResponse
	 * @param target 请求目标（请求路径）
	 * @return 是否处理成功
	 */
	public final boolean handle(HttpServletRequest req, HttpServletResponse res, String target) {
		final String method = req.getMethod();
		if (HuluSetting.isDevMode) {
			log.debug("Client [{}] {} [{}]", ServletUtil.getClientIP(req), method, target);
		}

		final ActionMethod actionMethod = actionMapping.getActionMethod(target);
		if (actionMethod == null || actionMethod.isHttpMethodMatch(method) == false) {
			//无对应的Action或者Http方法不匹配，跳过执行后续过程
			return false;
		}
		
		//找到对应的ActionMethod后注入ServletRequst和ServletResponse
		//在此阶段注入的目的是提升性能，避免没有匹配的ActionMethod依旧注入的问题
		fillReqAndRes(req, res);

		//重置过滤器执行游标，从第一个过滤器开始执行
		actionMethod.resetInterceptorPosition();
		try {
			actionMethod.invoke();
		} catch (ActionException e) {
			Render.renderError500(e.getCause());
		}finally{
			clearReqAndRes();
		}

		return true;
	}
	
	/**
	 * 注入ServletRequst和ServletResponse
	 * @param req ServletRequest
	 * @param res ServletResponse
	 */
	private static void fillReqAndRes(ServletRequest req, ServletResponse res){
		//-- 填充请求和响应对象到ActionContext本地线程
		Request.init((HttpServletRequest)req);
		Response.init((HttpServletResponse)res);
	}
	
	/**
	 * 清理线程Requst和Response
	 */
	private static void clearReqAndRes(){
		Request.clear();
		Response.clear();
	}
}

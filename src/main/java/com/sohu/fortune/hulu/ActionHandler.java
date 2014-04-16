package com.sohu.fortune.hulu;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import looly.github.hutool.ClassUtil;
import looly.github.hutool.CollectionUtil;
import looly.github.hutool.Log;
import looly.github.hutool.StrUtil;

import org.slf4j.Logger;

import com.sohu.fortune.hulu.exception.ActionException;
import com.sohu.fortune.hulu.interceptor.Interceptor;
import com.sohu.fortune.hulu.interceptor.InterceptorBuilder;
import com.sohu.fortune.hulu.render.ErrorRender;

/**
 * Action处理类
 * 
 * @author xiaoleilu
 */
public class ActionHandler {
	private final static Logger log = Log.get();

	/** Action方法映射 */
	private Map<String, ActionMethod> actionMethodMap = new HashMap<String, ActionMethod>();

	// -------------------------------------------------------------------- Constructor start
	public ActionHandler(String[] packageNames) {
		this.init(packageNames);
	}
	// -------------------------------------------------------------------- Constructor end

	/**
	 * 初始化<br>
	 * 查找指定包中类的所有方法，用于Action请求<br>
	 * 会排除Object对象中的public方法
	 * 
	 * @param packageNames Action类所在包的位置
	 * @throws Exception
	 */
	public final void init(String[] packageNames) {
		this.actionMethodMap = generateActionMap(packageNames);
	}

	/**
	 * 处理请求
	 * 
	 * @param target 请求目标（请求路径）
	 * @param request 请求对象
	 * @param response 响应对象
	 * @return 是否继续执行后续步骤
	 */
	public final boolean handle(String target) {
		if (HuluSetting.isDevMode) {
			log.debug("Client [{}] {} [{}]", Request.getIp(), ActionContext.getRequest().getMethod(), target);
		}

		final ActionMethod actionMethod = getActionMethod(target);

		if (actionMethod == null) {
			//无对应的Action，跳过执行后续过程
			return true;
		}

		//重置过滤器执行游标，从第一个过滤器开始执行
		actionMethod.resetInterceptorPosition();
		try {
			actionMethod.invoke();
		} catch (ActionException e) {
			ErrorRender.render500(e);
		}

		return false;
	}

	/**
	 * 获得请求路径对应的Action方法<br>
	 * 带参数时同时填充参数
	 * 
	 * @param path 请求路径（去掉ContextPath的）
	 * @return
	 */
	public final ActionMethod getActionMethod(String path) {
		// 不带参数的请求
		ActionMethod actionMethod = actionMethodMap.get(path);
		if (null != actionMethod) {
			return actionMethod;
		}
		
		// 带参数的请求
		final int lastSlashIndex = path.lastIndexOf(StrUtil.SLASH);
		if (lastSlashIndex != -1) {
			actionMethod = actionMethodMap.get(path.substring(0, lastSlashIndex));
			if (actionMethod != null) {
				Request.splitAndSetParams(path.substring(lastSlashIndex + 1));
			}
		}

		return actionMethod;
	}
	
	//------------------------------------------------------------- Private method start
	/**
	 * 初始化<br>
	 * 查找指定包中类的所有方法，用于Action请求<br>
	 * 会排除Object对象中的public方法
	 * 
	 * @param packageNames Action类所在包的位置
	 * @throws Exception
	 */
	private final Map<String, ActionMethod> generateActionMap(String[] packageNames) {
		Map<String, ActionMethod> actionMethodMap = new HashMap<String, ActionMethod>();
		
		//Object里的那些方法不能被识别成Action方法
		Set<String> forbiddenMethod = ClassUtil.getMethods(Object.class);

		Set<Class<?>> actionClasses = new HashSet<Class<?>>();
		for (String packageName : packageNames) {
			if(StrUtil.isBlank(packageName)) {
				continue;
			}
			actionClasses.addAll(ClassUtil.scanPackage(packageName.trim()));
		}
		
		if (HuluSetting.isDevMode) {
			for (Class<?> clazz : actionClasses) {
				log.debug("Finded Action class: [{}]", clazz.getName());
			}
		}
		
		for (Class<?> actionClass : actionClasses) {
			
			Object actionInstance = null;
			Method[] methods = null;
			try {
				// 每个Action单独捕获异常，这样不会影响其它Action
				actionInstance = actionClass.newInstance();
				methods = actionClass.getMethods();
			} catch (Exception e) {
				log.error(String.format("Init Action [%s] error!", actionClass.getName()), e);
				continue;
			}
			
			Interceptor[] actionInterceptors = InterceptorBuilder.build(actionClass);

			for (Method method : methods) {
				//过滤掉Object中的一些特殊方法(toString(), hash等等)，且只识别无参数方法，防止重载造成的语义混乱
				if (forbiddenMethod.contains(method.getName()) || method.getParameterTypes().length > 0) {
					continue;
				}
				
				//设置方法的拦截器
				Interceptor[] interceptors = CollectionUtil.addAll(actionInterceptors, InterceptorBuilder.build(method));
				
				ActionMethod actionMethod = new ActionMethod(actionInstance, method, interceptors);
				
				
				String key = actionMethod.getRequestPath();
				if(actionMethodMap.containsKey(key)) {
					//当有同一个请求路径对应不同的ActionMethod时，给出Log ERROR， 并不阻断初始化
					String errorMsg = StrUtil.format("Duplicate request path [{}]", key);
					log.error(errorMsg, new ActionException(errorMsg));
				}
				
				actionMethodMap.put(key, actionMethod);
				log.debug("Added action mapping: [{}]", key);
			}
		}
		return actionMethodMap;
	}
	//------------------------------------------------------------- Private method end
}

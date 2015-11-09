package com.xiaoleilu.hulu;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.xiaoleilu.hulu.exception.ActionException;
import com.xiaoleilu.hulu.interceptor.Interceptor;
import com.xiaoleilu.hulu.interceptor.InterceptorBuilder;
import com.xiaoleilu.hulu.render.ErrorRender;
import com.xiaoleilu.hutool.ClassUtil;
import com.xiaoleilu.hutool.ClassUtil.ClassFilter;
import com.xiaoleilu.hutool.CollectionUtil;
import com.xiaoleilu.hutool.StrUtil;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.StaticLog;

/**
 * Action处理类
 * 
 * @author xiaoleilu
 */
public class ActionHandler {
	private static Log log = StaticLog.get();

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
	 * @return 是否继续执行后续步骤
	 */
	public final boolean handle(String target) {
		if (HuluSetting.isDevMode) {
			log.debug("Client [{}] {} [{}]", Request.getIp(), ActionContext.getRequest().getMethod(), target);
		}

		final ActionMethod actionMethod = getActionMethod(target);
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

	/**
	 * 获得请求路径对应的Action方法<br>
	 * 带参数时同时填充参数
	 * 
	 * @param path 请求路径（去掉ContextPath的）
	 * @return ActionMethod
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
		final Map<String, ActionMethod> actionMethodMap = new HashMap<String, ActionMethod>();
		
		final Set<Class<?>> actionClasses = new HashSet<Class<?>>();
		final ClassFilter actionClassFilter = createActionClassFilter();	//Action类的过滤器，剔除不符合过滤条件的类
		for (String packageName : packageNames) {
			if(StrUtil.isBlank(packageName) == false) {
				actionClasses.addAll(ClassUtil.scanPackage(packageName.trim(), actionClassFilter));
			}
		}
		
		if (HuluSetting.isDevMode) {
			for (Class<?> clazz : actionClasses) {
				log.debug("Finded Action class: [{}]", clazz.getName());
			}
		}
		
		//Object里的那些方法不能被识别成Action方法
		final Set<String> forbiddenMethods = ClassUtil.getMethods(Object.class);
		
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
			
			final Interceptor[] actionInterceptors = InterceptorBuilder.build(actionClass);

			for (Method method : methods) {
				//过滤掉Object中的一些特殊方法(toString(), hash等等)，且只识别无参数方法，防止重载造成的语义混乱
				if (forbiddenMethods.contains(method.getName()) || method.getParameterTypes().length > 0) {
					continue;
				}
				
				//设置方法的拦截器（Action对象和方法的拦截器都对此方法有效）
				final Interceptor[] interceptors = CollectionUtil.addAll(actionInterceptors, InterceptorBuilder.build(method));
				
				final ActionMethod actionMethod = new ActionMethod(actionInstance, method, interceptors);
				
				
				final String key = actionMethod.getRequestPath();
				if(actionMethodMap.containsKey(key)) {
					//当有同一个请求路径对应不同的ActionMethod时，给出Log ERROR， 并不阻断初始化
					log.error(new ActionException(StrUtil.format("Duplicate request path [{}]", key)));
				}
				
				actionMethodMap.put(key, actionMethod);
				if(HuluSetting.isDevMode) {
					log.debug("Added action mapping: [{}]", key);
				}
			}
		}
		return actionMethodMap;
	}
	
	/**
	 * @return 创建Action类扫描过滤器
	 */
	private ClassFilter createActionClassFilter() {
		return new ClassFilter(){
			@Override
			public boolean accept(Class<?> clazz) {
				//过滤条件是必须为给定后缀
				return clazz.getSimpleName().endsWith(HuluSetting.actionSuffix);
			}
		};
	}
	
	//------------------------------------------------------------- Private method end
}

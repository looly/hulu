package com.xiaoleilu.hulu;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.xiaoleilu.hulu.exception.ActionException;
import com.xiaoleilu.hulu.interceptor.Interceptor;
import com.xiaoleilu.hulu.interceptor.InterceptorBuilder;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.StaticLog;
import com.xiaoleilu.hutool.util.ClassUtil;
import com.xiaoleilu.hutool.util.ClassUtil.ClassFilter;
import com.xiaoleilu.hutool.util.CollectionUtil;
import com.xiaoleilu.hutool.util.StrUtil;

/**
 * Action映射表
 * @author Looly
 *
 */
public class ActionMapping extends HashMap<String, ActionMethod>{
	private static final long serialVersionUID = -6041272411190069856L;
	private static Log log = StaticLog.get();
	
	// -------------------------------------------------------------------- Constructor start
	public ActionMapping(String[] packageNames) {
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
		final Set<Class<?>> actionClasses = new HashSet<Class<?>>();
		final ClassFilter actionClassFilter = createActionClassFilter();	//Action类的过滤器，剔除不符合过滤条件的类
		for (String packageName : packageNames) {
			if(StrUtil.isNotBlank(packageName)) {
				actionClasses.addAll(ClassUtil.scanPackage(packageName.trim(), actionClassFilter));
			}
		}
		
		if (HuluSetting.isDevMode) {
			for (Class<?> clazz : actionClasses) {
				log.debug("Finded Action class: [{}]", clazz.getName());
			}
		}
		
		//Object里的那些方法不能被识别成Action方法
		final Set<String> forbiddenMethods = ClassUtil.getPublicMethodNames(Object.class);
		
		Object actionInstance = null;
		Method[] methods = null;
		for (Class<?> actionClass : actionClasses) {
			try {
				// 每个Action单独捕获异常，这样不会影响其它Action
				actionInstance = actionClass.newInstance();
				methods = actionClass.getMethods();
			} catch (Exception e) {
				log.error(e, String.format("Init Action [%s] error!", actionClass.getName()));
				continue;
			}
			
			final Interceptor[] actionInterceptors = InterceptorBuilder.build(actionClass);
			for (Method method : methods) {
				
				//过滤掉Object中的一些特殊方法(toString(), hash等等)
				if (forbiddenMethods.contains(method.getName())) {
					continue;
				}
				
				//设置方法的拦截器（Action对象和方法的拦截器都对此方法有效）
				final Interceptor[] interceptors = CollectionUtil.addAll(actionInterceptors, InterceptorBuilder.build(method));
				final ActionMethod actionMethod = new ActionMethod(actionInstance, method, interceptors);
				
				final String key = actionMethod.getRequestPath();
				if(this.containsKey(key)) {
					//当有同一个请求路径对应不同的ActionMethod时，给出Log ERROR， 并不阻断初始化
					log.warn(new ActionException(StrUtil.format("Duplicate request path [{}]", key)));
				}else{
					this.put(key, actionMethod);
					if(HuluSetting.isDevMode) {
						log.debug("Added action mapping: [{}]", key);
					}
				}
			}
		}
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
		ActionMethod actionMethod = this.get(path);
		if (null != actionMethod) {
			return actionMethod;
		}
		
		// 带参数的请求
		final int lastSlashIndex = path.lastIndexOf(StrUtil.SLASH);
		if (lastSlashIndex != -1) {
			actionMethod = this.get(path.substring(0, lastSlashIndex));
			if (actionMethod != null) {
				Request.splitAndSetParams(path.substring(lastSlashIndex + 1));
			}
		}

		return actionMethod;
	}
	
	//------------------------------------------------------------- Private method start

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

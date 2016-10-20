package com.xiaoleilu.huludemo.action;

import java.util.Collection;

import com.xiaoleilu.hulu.Render;
import com.xiaoleilu.hulu.Request;
import com.xiaoleilu.hulu.annotation.Route;
import com.xiaoleilu.hulu.exception.DaoException;
import com.xiaoleilu.hulu.interceptor.Intercept;
import com.xiaoleilu.huludemo.dao.VirtualDao;
import com.xiaoleilu.huludemo.interceptor.Log1Interceptor;
import com.xiaoleilu.huludemo.interceptor.Log2Interceptor;
import com.xiaoleilu.huludemo.po.User;
import com.xiaoleilu.hutool.json.JSONUtil;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import com.xiaoleilu.hutool.util.StrUtil;

/**
 * 用户相操作的Action<br>
 * 在hulu.setting中设置被扫描的包，这个包下所有的*Action类会被当作Action对象，里面的无返回值的Public方法会被当作一个Action<br>
 * 
 * 在访问的时候规则是这样的，你的UserAction类下有个create方法，那么访问路径就是/user/create<br>
 * 假如你用了@Route注解，那么注解的值就是访问的路径
 * @author loolly
 *
 */
public class UserAction {
	/** 这是我自己封装的Log，直接get，不需要传递类名了，很省事儿*/
	private final static Log log = LogFactory.get();
	
	/**
	 * 创建用户Action
	 */
	public void create() {
		//这是Request类中提供的一个静态方法，通过反射获取参数，调用POJO对象的set方法填充参数
		//例如：你的参数名称为name，那么user.setName方法会被调用。当第二个参数为true时，你的参数名必须是user.name
		final User user = Request.getBean(User.class);
		
		//我爱死Slf4J这种字符串模板了，根本停不下来
		log.debug("Create user {}", user);
		
		try {
			VirtualDao.getInstance().saveUser(user);
		} catch (DaoException e) {
			//ErrorRender这个类用于返回给客户端（浏览器）一个错误信息，例如render500是返回一个500页面。内容是堆栈信息。
			//其实这个try catch不是必须的，可以在create方法上抛出其，当hulu.setting中的DevMode打开（为true）的时候，会自动返回一个带堆栈信息的500页面。
			Render.renderError500(e);
			return;
		}
		
		//Render这个类用于返回正常内容，例如HTML、JSP、JSON、JSONP等，在此返回一个普通文本。
		Render.renderText(StrUtil.format("Create user [{}] OK!", user.getName()));
	}
	
	/**
	 * 列出所有用户
	 */
	@Route("/listUser")		//这个注解用于指定Action请求路径
	@Intercept({Log1Interceptor.class, Log2Interceptor.class})	//两个过滤器
	public void list() {
		final Collection<User> users = VirtualDao.getInstance().listUsers();
		Render.renderJson(JSONUtil.toJsonPrettyStr(users));
	}
	
	/**
	 * 列出所有用户（只允许POST方式）
	 */
	@Route("post:/listUserPost")		//这个注解用于指定Request方法名和请求路径
	public void listPost() {
		final Collection<User> users = VirtualDao.getInstance().listUsers();
		Render.renderJson(JSONUtil.toJsonPrettyStr(users));
	}
}

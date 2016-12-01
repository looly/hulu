package com.xiaoleilu.hulu.server;

import java.io.File;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import com.xiaoleilu.hulu.ActionServlet;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import com.xiaoleilu.hutool.setting.Setting;
import com.xiaoleilu.hutool.util.StrUtil;

/**
 * 内嵌Jetty服务器配置
 * 
 * @author Looly
 *
 */
public class JettySetting {
	private static final Log log = LogFactory.get();
	private static Setting setting = new Setting("config/jetty.setting");
	
	private static String resourceBase = getResourceBase();

	/**
	 * 创建QueuedThreadPool
	 * 
	 * @return QueuedThreadPool
	 */
	public static QueuedThreadPool createQueuedThreadPool() {
		QueuedThreadPool threadPool = new QueuedThreadPool();

		threadPool.setMinThreads(setting.getInt("min", "threads", 100));
		threadPool.setMaxThreads(setting.getInt("max", "threads", 500));

		return threadPool;
	}

	/**
	 * 创建HttpConfiguration
	 * 
	 * @return HttpConfiguration
	 */
	public static HttpConfiguration createHttpConfiguration() {
		HttpConfiguration httpConfig = new HttpConfiguration();

		httpConfig.setSecurePort(setting.getInt("secure-port", "http", 8443));
		httpConfig.setOutputBufferSize(setting.getInt("output-buffersize", "http", 32768));
		httpConfig.setRequestHeaderSize(setting.getInt("request-headersize", "http", 8192));
		httpConfig.setResponseHeaderSize(setting.getInt("response-headersize", "http", 8192));
		httpConfig.setSendServerVersion(true);
		httpConfig.setSendDateHeader(false);

		return httpConfig;
	}

	/**
	 * 创建HttpConnectionFactory
	 * 
	 * @return HttpConnectionFactory
	 */
	public static HttpConnectionFactory createHttpConnectionFactory() {
		return new HttpConnectionFactory(createHttpConfiguration());
	}

	/**
	 * 创建WebAppContext
	 * 
	 * @return WebAppContext
	 */
	public static WebAppContext createWebAppContext() {
		WebAppContext webAppContext = new WebAppContext();

		webAppContext.setContextPath(setting.getStr("contextPath", "/"));
		webAppContext.setResourceBase(resourceBase);

		// 方式1：Servlet级别拦截
		webAppContext.addServlet(createHuluServletHolder(), "/*");

		// 方式2：Filter级别拦截
		// FilterHolder filterHolder = new FilterHolder(ActionFilter.class);
		// filterHolder.setAsyncSupported(false);
		// webAppContext.addFilter(ActionFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

		return webAppContext;
	}
	
	/**
	 * 创建Servlet处理器
	 * @return Servlet处理器
	 */
	public static ServletHandler createServletHandler(){
		ServletHandler servletHandler = new ServletHandler();
		servletHandler.addServletWithMapping(createHuluServletHolder(), "/*");
		return servletHandler;
	}

	/**
	 * 静态资源处理器
	 * 
	 * @return 静态资源处理器
	 */
	public static ResourceHandler createResourceHandler() {
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase(resourceBase);
		return resourceHandler;
	}

	/**
	 * 创建ServerConnector并加入到Jetty Server中
	 * 
	 * @param server Jetty Server
	 * @return ServerConnector
	 */
	public static ServerConnector createServerConnectorAndAddToServer(Server server) {
		ServerConnector connector = new ServerConnector(server, JettySetting.createHttpConnectionFactory());

		connector.setHost(setting.getStr("host", "http", "0.0.0.0"));
		connector.setPort(setting.getInt("port", "http", 8090));
		connector.setIdleTimeout(setting.getLong("idle-timeout", "http", 30000L));
		server.addConnector(connector);

		return connector;
	}
	
	/**
	 * @return Jetty 配置类
	 */
	public static Setting getSetting() {
		return setting;
	}

	//----------------------------------------------------------------------------- Private method start
	/**
	 * 读取resourceBase
	 * 
	 * @return resourceBase
	 */
	private static String getResourceBase() {
		String resourceBase = setting.getStr("webRoot");
		if (StrUtil.isEmpty(resourceBase)) {
			resourceBase = "./src/main/webapp";// 用于Maven测试
			File file = new File(resourceBase);
			if (false == file.exists() || false == file.isDirectory()) {
				resourceBase = ".";
			}
		}
		log.debug("Jetty resource base: [{}]", resourceBase);
		return resourceBase;// 当前目录，用于部署环境
	}
	
	/**
	 * 创建Hulu的ServletHolder
	 * @return Hulu的ServletHolder
	 */
	private static ServletHolder createHuluServletHolder(){
		ServletHolder servletHolder = new ServletHolder(ActionServlet.class);
		servletHolder.setAsyncSupported(false);
		servletHolder.setInitOrder(1);
		return servletHolder;
	}
	//----------------------------------------------------------------------------- Private method end
}

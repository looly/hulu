package com.xiaoleilu.hulu.server;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import com.xiaoleilu.hutool.setting.Setting;

/**
 * 内嵌Jetty服务器配置
 * @author Looly
 *
 */
public class JettySetting {
	private static Setting setting = new Setting("config/jetty.setting");
	
	/**
	 * 创建QueuedThreadPool
	 * @return QueuedThreadPool
	 */
	public static QueuedThreadPool createQueuedThreadPool(){
		QueuedThreadPool threadPool = new QueuedThreadPool();
		
		threadPool.setMinThreads(setting.getInt("min", "threads", 100));
		threadPool.setMaxThreads(setting.getInt("max", "threads", 500));
		
		return threadPool;
	}
	
	/**
	 * 创建HttpConfiguration
	 * @return HttpConfiguration
	 */
	public static HttpConfiguration createHttpConfiguration(){
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
	 * @return HttpConnectionFactory
	 */
	public static HttpConnectionFactory createHttpConnectionFactory(){
		return new HttpConnectionFactory(createHttpConfiguration());
	}
	
	/**
	 * 创建WebAppContext
	 * @return WebAppContext
	 */
	public static WebAppContext createWebAppContext(){
		WebAppContext webAppContext = new WebAppContext();
		
		webAppContext.setContextPath(setting.getStr("contextPath", "/"));
		webAppContext.setResourceBase(setting.getStr("webRoot", "./webapp"));
		
		return webAppContext;
	}
	
	/**
	 * 创建ServerConnector并加入到Jetty Server中
	 * @param server Jetty Server
	 * @return ServerConnector
	 */
	public static ServerConnector createServerConnectorAndAddToServer(Server server){
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
	public static Setting getSetting(){
		return setting;
	}
}

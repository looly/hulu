package com.xiaoleilu.hulu.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import com.xiaoleilu.hulu.ActionServlet;
import com.xiaoleilu.hulu.exception.ServerException;
import com.xiaoleilu.hulu.exception.ServerRuntimeException;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;

/**
 * 内嵌的Jetty应用服务器
 * @author Looly
 *
 */
public class EmbedJettyServer {
	private static final Log log = LogFactory.get();

	private Server server;
	private WebAppContext webAppContext;
	
	/**
	 * 启动Jetty服务器
	 * @param join 是否同步阻断
	 */
	public static void startup(boolean join){
		try {
			new EmbedJettyServer().start(join);
		} catch (ServerException e) {
			throw new ServerRuntimeException(e);
		}
	}

	/**
	 * 构造
	 */
	public EmbedJettyServer() {
		//禁用Jsp
		System.setProperty("org.apache.jasper.compiler.disablejsr199", "true");
	}
	
	/**
	 * 启动，不同步阻断
	 * @throws ServerException
	 */
	public void start() throws ServerException {
		start(false);
	}
	
	/**
	 * 启动
	 * @param join 是否同步阻断
	 * @throws ServerException
	 */
	public void start(boolean join) throws ServerException {
		server = new Server(JettySetting.createQueuedThreadPool());
		
		// JVM退出时关闭Jetty
		server.setStopAtShutdown(true);
		
		//初始化Connector
		ServerConnector serverConnector = JettySetting.createServerConnectorAndAddToServer(server);

		//初始化WebAppContext
		webAppContext = JettySetting.createWebAppContext();
		
		//方式1：Servlet级别拦截
		ServletHolder servletHolder = new ServletHolder(ActionServlet.class);
		servletHolder.setAsyncSupported(false);
		servletHolder.setInitOrder(1);
		webAppContext.addServlet(servletHolder, "/*");
		
		//方式2：Filter级别拦截
//		FilterHolder filterHolder = new FilterHolder(ActionFilter.class);
//		filterHolder.setAsyncSupported(false);
//		webAppContext.addFilter(ActionFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

		//初始化Handler
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { webAppContext, new DefaultHandler() });
		server.setHandler(handlers);

		try {
			server.start();
			log.info("Jetty Server for Hulu Listen on {}: {}", serverConnector.getHost(), serverConnector.getPort());
			if(join){
				server.join();
			}
		} catch (Exception e) {
			throw new ServerException(e);
		}
	}

	/**
	 * 关闭
	 * @throws ServerException
	 */
	public void stop() throws ServerException {
		try {
			server.stop();
		} catch (Exception e) {
			throw new ServerException(e);
		}
	}
	
	public static void main(String[] args) {
		startup(true);
	}
}
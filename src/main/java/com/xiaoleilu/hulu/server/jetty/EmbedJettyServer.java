package com.xiaoleilu.hulu.server.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;

import com.xiaoleilu.hulu.exception.ServerException;
import com.xiaoleilu.hulu.exception.ServerRuntimeException;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 内嵌的Jetty应用服务器
 * @author Looly
 *
 */
public class EmbedJettyServer {
	private static final Log log = LogFactory.get();

	private Server server;
	
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

		//初始化Handler
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] {
				JettySetting.createResourceHandler(),	//静态资源处理
				JettySetting.createWebAppContext(), 	//Servlet处理
				new DefaultHandler() 							//	默认处理
		});
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
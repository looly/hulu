package com.xiaoleilu.hulu.server.undertow;

import javax.servlet.ServletException;

import com.xiaoleilu.hulu.ActionServlet;
import com.xiaoleilu.hulu.exception.ServerRuntimeException;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletInfo;

public class EmbedUndertowServer {
	private static final Log log = LogFactory.get();
	
	/**
	 * 启动服务器
	 */
	public static void startup(){
		new EmbedUndertowServer().start();
	}
	
	/**
	 * 启动
	 */
	public void start(){
		ServletInfo servletInfo = Servlets.servlet(ActionServlet.class).addMapping("/*");
		
		String contextPath = UndertowSetting.getSetting().getStr("contextPath", "/");
		
		DeploymentInfo deploymentInfo = Servlets.deployment()
				.setClassLoader(ClassUtil.getClassLoader())
				.setDeploymentName(UndertowSetting.getSetting().getStr("deploymentName", "hulu.war"))
				.setContextPath(contextPath)
				.addServlets(servletInfo);
		
		DeploymentManager manager = Servlets.defaultContainer().addDeployment(deploymentInfo);
		manager.deploy();
		
		HttpHandler httpHandler;
		try {
			httpHandler = manager.start();
		} catch (ServletException e) {
			throw new ServerRuntimeException(e);
		}
		
		PathHandler handler = Handlers.path(Handlers.redirect(contextPath)).addPrefixPath(contextPath, httpHandler);
		
		String host = UndertowSetting.getSetting().getStr("host", "localhost");
		int port = UndertowSetting.getSetting().getInt("port", 8090);
		
		Undertow server = Undertow.builder()
				.addHttpListener(port, host)
				.setHandler(handler)
				.build();
		
		server.start();
		log.info("Undertow Server for Hulu Listen on {}: {}", host, port);
	}
	
	public static void main(String[] args) {
		startup();
	}
}

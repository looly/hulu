package com.xiaoleilu.huludemo;

import com.xiaoleilu.hulu.exception.ServerException;
import com.xiaoleilu.hulu.server.jetty.EmbedJettyServer;

/**
 * 内嵌的Jetty服务器
 * @author Looly
 *
 */
public class Server {
	public static void main(String[] args) {
		String serverName = "jetty";
		
		switch (serverName) {
			case "jetty":
				EmbedJettyServer.startup(true);
				break;
			case "tomcat":
				break;
			default:
				break;
		}
	}
}

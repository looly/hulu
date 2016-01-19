package com.xiaoleilu.hulu;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.StaticLog;
import com.xiaoleilu.hutool.util.DateUtil;

/**
 * 框架初始化监听
 * @author Looly
 *
 */
@WebListener
public class ActionListener implements ServletContextListener{
	private static Log log = StaticLog.get();
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		final long start = System.currentTimeMillis();
		
		ActionContext.init(sce.getServletContext());
		
		log.info("***** Hulu framwork init finished, spend {}ms *****", DateUtil.spendMs(start));
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		log.info("***** Hulu framwork over. *****");
	}
	
}

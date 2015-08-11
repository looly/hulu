package com.xiaoleilu.hulu;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;

import com.xiaoleilu.hutool.DateUtil;
import com.xiaoleilu.hutool.Log;

/**
 * 框架初始化监听
 * @author Looly
 *
 */
@WebListener
public class ActionListener implements ServletContextListener{
	private final static Logger log = Log.get();
	
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

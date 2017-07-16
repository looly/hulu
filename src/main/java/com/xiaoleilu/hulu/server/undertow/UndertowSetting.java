package com.xiaoleilu.hulu.server.undertow;

import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import com.xiaoleilu.hutool.setting.Setting;

/**
 * 内嵌Undertow服务器配置
 * 
 * @author Looly
 *
 */
public class UndertowSetting {
	private static final Log log = LogFactory.get();
	private static Setting setting;
	
	static{
		try {
			setting = new Setting("config/undertow.setting");
		} catch (Exception e) {
			log.warn(e.getMessage());
			setting = new Setting();
		}
	}
	
	/**
	 * @return Jetty 配置类
	 */
	public static Setting getSetting() {
		return setting;
	}
	
	//----------------------------------------------------------------------------- Private method start
	
	//----------------------------------------------------------------------------- Private method end
}

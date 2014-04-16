package com.sohu.fortune.hulu;

import java.net.URL;

import looly.github.hutool.Log;
import looly.github.hutool.Setting;
import looly.github.hutool.URLUtil;

import org.slf4j.Logger;

/**
 * 全局设定文件
 * @author xiaoleilu
 *
 */
public class HuluSetting {
	private static Logger log = Log.get();
	
	/** 默认的字符集编码 */
	public final static String DEFAULT_CHARSET = "utf-8";
	/** 默认的URL参数分隔符 */
	public final static String DEFAULT_URL_PARAM_SEPERATOR = ",";
	/** 默认的配置文件路径（相对ClassPath）*/
	public final static String DEFAULT_SETTING_PATH = "config/hulu.setting";
	
	/** 字符编码 */
	public static String charset = DEFAULT_CHARSET;
	/** URL参数的分隔符 */
	public static String urlParamSeparator = DEFAULT_URL_PARAM_SEPERATOR;
	/** Action 包 */
	public static String[] actionPackages = new String[]{""};
	
	/** 是否为开发模式 */
	public static boolean isDevMode;
	
	static {
		load();
	}
	
	/**
	 * 加载全局设定
	 */
	public static void load() {
		load(DEFAULT_SETTING_PATH);
	}
	
	/**
	 * 加载全局设定
	 * @param settingPath 设定文件路径，相对Classpath
	 */
	synchronized public static void load(String settingPath) {
		URL url = URLUtil.getURL(settingPath);
		if(url == null) {
			log.warn("Can not find Hulu setting file [{}], use default setting.", settingPath);
			return;
		}
		Setting setting = new Setting(url, DEFAULT_CHARSET, true);
		
		charset = setting.getStringWithDefault("charset", DEFAULT_CHARSET);
		urlParamSeparator = setting.getStringWithDefault("url.param.separator", DEFAULT_URL_PARAM_SEPERATOR);
		actionPackages = setting.getStrings("package.action");
		
		isDevMode = setting.getBool("mode.dev");
	}
}

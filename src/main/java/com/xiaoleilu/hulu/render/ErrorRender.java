package com.xiaoleilu.hulu.render;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.xiaoleilu.hulu.ActionContext;
import com.xiaoleilu.hulu.HuluSetting;
import com.xiaoleilu.hulu.Request;
import com.xiaoleilu.hutool.Log;
import com.xiaoleilu.hutool.StrUtil;

/**
 * 生成和处理Action返回错误结果的类
 * @author xiaoleilu
 *
 */
public class ErrorRender {
	private static Logger log = Log.get();
	
	private final static String TEMPLATE_ERROR = "<!DOCTYPE html><html><head><title>Hulu framework - Error report</title><style>h1,h3 {color:white; background-color: gray;}</style></head><body><h1>HTTP Status {} - {}</h1><hr size=\"1\" noshade=\"noshade\" /><p>{}</p><hr size=\"1\" noshade=\"noshade\" /><h3>Hulu framework</h3></body></html>";
	
	/**
	 * 将错误发送到容器
	 * @param errorCode 错误代码
	 * @param errorContent 错误信息
	 */
	public static void sendError(int errorCode, String errorContent) {
		HttpServletResponse response = ActionContext.getResponse();
		try {
			if(HuluSetting.isDevMode) {
				response.sendError(errorCode);
			}else {
				response.sendError(errorCode, errorContent);
			}
		} catch (IOException e) {
			log.error("Error when sendError!", e);
		}
	}
	
	/**
	 * 将错误发送到容器
	 * @param errorCode 错误代码
	 * @param errorContent 错误信息
	 */
	public static void render(int errorCode, String errorContent) {
		HttpServletResponse response = ActionContext.getResponse();
		response.setStatus(errorCode);
		Render.render(errorContent, Render.CONTENT_TYPE_HTML, response);
	}
	
	/**
	 * 在页面打印堆栈信息<br>
	 * @param e 异常
	 */
	public static void render500(Throwable e) {
		//500错误打印到错误日志
		log.error("500 error!", e);
		
		if(HuluSetting.isDevMode == false) {
			sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "500 Server Error!");
			return;
		}
		
		final StringWriter writer = new StringWriter();
		// 把错误堆栈储存到流中
		e.printStackTrace(new PrintWriter(writer));
		String content = writer.toString().replace("\tat", "&nbsp;&nbsp;&nbsp;&nbsp;\tat");
		content = content.replace("\n", "<br/>\n");
		content = StrUtil.format(TEMPLATE_ERROR, 500, Request.getServletRequest().getRequestURI(), content);
		render(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, content);
	}
	
	/**
	 * 输出404信息
	 */
	public static void render404() {
		sendError(HttpServletResponse.SC_NOT_FOUND, "404 Not Found!");
	}
}

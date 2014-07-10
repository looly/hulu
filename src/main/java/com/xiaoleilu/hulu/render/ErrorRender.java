package com.xiaoleilu.hulu.render;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.xiaoleilu.hulu.ActionContext;
import com.xiaoleilu.hulu.HuluSetting;
import com.xiaoleilu.hutool.Log;

/**
 * 生成和处理Action返回错误结果的类
 * @author xiaoleilu
 *
 */
public class ErrorRender {
	private static Logger log = Log.get();
	
	private final static TEMPLATE_ERROR = "";
	
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
	 * TODO 500页面内容
	 * @param e 异常
	 */
	public static void render500(Exception e) {
		//500错误打印到错误日志
		log.error("500 error!", e);
		
		if(HuluSetting.isDevMode == false) {
			sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "500 Server Error!");
			return;
		}
		
		final StringWriter writer = new StringWriter();
		// 把错误堆栈储存到流中
		e.printStackTrace(new PrintWriter(writer));
		render(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, writer.toString().replace("\tat", "<br/>&nbsp;&nbsp;&nbsp;&nbsp;\tat"));
	}
	
	/**
	 * 输出404信息
	 *TODO 404页面内容
	 */
	public static void render404() {
		sendError(HttpServletResponse.SC_NOT_FOUND, "404 Not Found!");
	}
}

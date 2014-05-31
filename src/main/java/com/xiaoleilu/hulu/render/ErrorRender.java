package com.xiaoleilu.hulu.render;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

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
			render(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "500 Server Error!");
		}
		
		final ByteArrayOutputStream ostr = new ByteArrayOutputStream();	//无需关闭
		try {
			// 把错误堆栈储存到流中
			e.printStackTrace(new PrintStream(ostr));
			render(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ostr.toString(HuluSetting.charset).replace("\tat", "<br/>&nbsp;&nbsp;&nbsp;&nbsp;\tat"));
		} catch (IOException e2) {
			log.error("Error when output to client!", e2);
		}
	}
	
	/**
	 * 输出404信息
	 *TODO 404页面内容
	 */
	public static void render404() {
		render(HttpServletResponse.SC_NOT_FOUND, "404 Not Found!");
	}
}

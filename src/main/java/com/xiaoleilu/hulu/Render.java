package com.xiaoleilu.hulu;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import com.xiaoleilu.hulu.view.Error404View;
import com.xiaoleilu.hulu.view.Error500View;
import com.xiaoleilu.hulu.view.ErrorView;
import com.xiaoleilu.hulu.view.FileView;
import com.xiaoleilu.hulu.view.ForwardView;
import com.xiaoleilu.hulu.view.HtmlView;
import com.xiaoleilu.hulu.view.JsView;
import com.xiaoleilu.hulu.view.JsonView;
import com.xiaoleilu.hulu.view.JspView;
import com.xiaoleilu.hulu.view.Redirect301View;
import com.xiaoleilu.hulu.view.RedirectView;
import com.xiaoleilu.hulu.view.TextView;
import com.xiaoleilu.hulu.view.VelocityView;
import com.xiaoleilu.hulu.view.View;
import com.xiaoleilu.hulu.view.XmlView;
import com.xiaoleilu.hutool.io.IoUtil;
import com.xiaoleilu.hutool.json.JSON;

/**
 * 生成和处理Action返回结果的类
 * 
 * @author xiaolilu
 * 
 */
public class Render {
	
	/**
	 * 重定向到一个新地址<br>
	 * status 302
	 * 
	 * @param uri 新地址
	 * @throws IOException
	 */
	public static final void redirect(String uri) {
		new RedirectView(uri).render();
	}
	
	/**
	 * 301跳转，默认附带参数
	 * 
	 * @param url 跳转的URL
	 */
	public static void redirect301(String url) {
		new Redirect301View(url, true).render();
	}

	/**
	 * 301跳转
	 * 
	 * @param url 跳转的URL
	 * @param isWithParamStr 是否包含URL参数
	 */
	public static void redirect301(String url, boolean isWithParamStr) {
		new Redirect301View(url, isWithParamStr).render();
	}

	/**
	 * 服务端跳转
	 * 
	 * @param uri 跳转的URI
	 * @throws ServletException
	 * @throws IOException
	 */
	public static final void forward(String uri) {
		new ForwardView(uri).render();
	}

	/**
	 * 返回普通文本
	 * 
	 * @param text 文本
	 */
	public static void renderText(String text) {
		new TextView(text).render();
	}

	/**
	 * 返回HTML
	 * 
	 * @param html HTML
	 */
	public static void renderHtml(String html) {
		new HtmlView(html).render();
	}

	/**
	 * 返回XML
	 * 
	 * @param xml XML
	 */
	public static void renderXml(String xml) {
		new XmlView(xml).render();
	}
	
	/**
	 * 返回JSON
	 * 
	 * @param jsonStr JSON字符串
	 */
	public static void renderJson(String jsonStr) {
		new JsonView(jsonStr).render();
	}
	
	/**
	 * 返回JSON
	 * 
	 * @param json JSONObject or JSONArray
	 */
	public static void renderJson(JSON json) {
		renderJson(json.toString());
	}
	
	/**
	 * 返回JS<br>
	 * 此方法会自动获取callback参数，并返回以callback参数值命名的函数
	 * @param data 返回的js数据
	 */
	public static void renderJs(String data) {
		renderJs(data, null);
	}

	/**
	 * 返回JS<br>
	 * 此方法会自动获取callback参数，并返回以callback参数值命名的函数
	 * @param data 返回的js数据
	 * @param callbackParamName 回调函数的参数名称
	 */
	public static void renderJs(String data, String callbackParamName) {
		new JsView(data, callbackParamName).render();
	}

	/**
	 * 返回Velocity处理后的HTML内容<br>
	 * 模板的参数全部来自于Request 的Parameter，如需自定义，请填充其值
	 * @param templateFileName 模板文件
	 */
	public static void renderVelocityHtml(String templateFileName) {
		renderVelocity(templateFileName, Response.CONTENT_TYPE_HTML);
	}

	/**
	 * 返回Velocity处理后的内容<br>
	 * 模板的参数全部来自于Request 的Parameter，如需自定义，请填充其值
	 * @param templateFileName 模板文件
	 * @param contentType 文件类型
	 */
	public static void renderVelocity(String templateFileName, String contentType) {
		new VelocityView(templateFileName, contentType).render();
	}

	/**
	 * 响应文件<br>
	 * 文件过大将下载失败
	 * @param file 文件对象
	 */
	public static void renderFile(File file) {
		renderFile(file, null, IoUtil.DEFAULT_BUFFER_SIZE);
	}

	/**
	 * 响应文件<br>
	 * 文件过大将下载失败
	 * @param file 文件对象
	 * @param responseFileName 响应给客户端的文件名，如果为空则使用编码后的原文件名
	 * @param bufferSize 缓存大小
	 */
	public static void renderFile(File file, String responseFileName, int bufferSize) {
		new FileView(file, responseFileName, bufferSize).render();
	
	}
	
	/**
	 * 响应到Jsp文件
	 * @param view jsp的url
	 */
	public static void renderJsp(String view) {
		new JspView(view).render();
	}
	
	//----------------------------------------------------------------------------------- Error Render
	/**
	 * 返回错误页面
	 * @param errorCode 错误代码
	 * @param errorContent 错误信息
	 */
	public static void renderError(int errorCode, String errorContent) {
		new ErrorView(errorCode, errorContent).render();
	}
	
	/**
	 * 在页面打印堆栈信息<br>
	 * @param e 异常
	 */
	public static void renderError500(Throwable e) {
		new Error500View(e).render();
	}
	
	/**
	 * 在页面打印堆栈信息<br>
	 * @param errorMsg 异常消息
	 */
	public static void renderError500(String errorMsg) {
		new Error500View(errorMsg).render();
	}
	
	/**
	 * 输出404信息
	 */
	public static void renderError404() {
		new Error404View().render();
	}
	
	/**
	 * 输出404信息
	 * @param pageContent 错误消息页面内容
	 */
	public static void render404Page(String pageContent) {
		new Error404View(pageContent, true).render();
	}
	
	/**
	 * 输出404信息
	 * @param content 错误消息内容
	 */
	public static void render404(String content) {
		new Error404View(content).render();
	}
	
	/**
	 * 写入View到客户端
	 * @param view View
	 */
	public static void render(View view){
		view.render();
	}
}

package com.xiaoleilu.hulu.render.view;

import javax.servlet.http.HttpServletResponse;

import com.xiaoleilu.hulu.HuluSetting;
import com.xiaoleilu.hulu.Request;
import com.xiaoleilu.hulu.Response;
import com.xiaoleilu.hutool.exceptions.ExceptionUtil;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import com.xiaoleilu.hutool.util.StrUtil;

/**
 * 500错误视图
 * @author Looly
 *
 */
public class Error500View extends ErrorView{
	private static final Log log = LogFactory.get();
	
	private final static String TEMPLATE_ERROR = "<!DOCTYPE html><html><head><title>Hulu framework - Error report</title><style>h1,h3 {color:white; background-color: gray;}</style></head><body><h1>HTTP Status {} - {}</h1><hr size=\"1\" noshade=\"noshade\" /><p>{}</p><hr size=\"1\" noshade=\"noshade\" /><h3>Hulu framework</h3></body></html>";
	
	private Throwable e;
	
	//---------------------------------------------------------- Constructor start
	public Error500View() {
		this(null);
	}

	public Error500View(Throwable e) {
		super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "500 Server Error!");
		this.e = e;
	}
	//---------------------------------------------------------- Constructor end
	
	//---------------------------------------------------------- Getters and Setters start
	public Throwable getE() {
		return e;
	}
	public void setE(Throwable e) {
		this.e = e;
	}
	//---------------------------------------------------------- Getters and Setters end

	@Override
	public void render() {
		//500错误打印到错误日志
		log.error("500 error!", e);
		
		if(false == HuluSetting.isDevMode){
			Response.sendError(getErrorCode(), getErrorContent());
			return;
		}
		

		String content = ExceptionUtil.stacktraceToString(e)
				.replace("\tat", "&nbsp;&nbsp;&nbsp;&nbsp;\tat")
				.replace("\n", "<br/>\n");
		content = StrUtil.format(TEMPLATE_ERROR, this.getErrorCode(), Request.getServletRequest().getRequestURI(), content);
		setErrorContent(content);
		super.render();
	}
	
	@Override
	public String toString() {
		return ExceptionUtil.stacktraceToString(this.e);
	}
}

package com.xiaoleilu.hulu.render.view;

import com.xiaoleilu.hulu.Response;

/**
 * HTML内容
 * @author Looly
 *
 */
public class HtmlView extends ContentTypeView{
	
	public HtmlView() {
		this(null);
	}
	
	public HtmlView(String text) {
		super(text, Response.CONTENT_TYPE_HTML);
	}
}

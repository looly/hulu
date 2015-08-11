package com.xiaoleilu.hulu.render.view;

import com.xiaoleilu.hulu.render.Render;

/**
 * HTML内容
 * @author Looly
 *
 */
public class HtmlView extends ContentTypeView{
	
	public HtmlView() {
	}
	
	public HtmlView(String text) {
		super(text, Render.CONTENT_TYPE_HTML);
	}
}

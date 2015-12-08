package com.xiaoleilu.hulu.render.view;

import com.xiaoleilu.hulu.render.Render;

/**
 * HTML内容
 * @author Looly
 *
 */
public class XmlView extends ContentTypeView{
	
	//---------------------------------------------------------- Constructor start
	public XmlView() {
		this.contentType = Render.CONTENT_TYPE_XML;
	}
	
	public XmlView(String text) {
		super(text, Render.CONTENT_TYPE_XML);
	}
	//---------------------------------------------------------- Constructor start
}

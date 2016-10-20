package com.xiaoleilu.hulu.view;

import com.xiaoleilu.hulu.Response;

/**
 * HTML内容
 * @author Looly
 *
 */
public class XmlView extends ContentTypeView{
	
	//---------------------------------------------------------- Constructor start
	public XmlView() {
		this.contentType = Response.CONTENT_TYPE_XML;
	}
	
	public XmlView(String text) {
		super(text, Response.CONTENT_TYPE_XML);
	}
	//---------------------------------------------------------- Constructor start
}

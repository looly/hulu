package com.xiaoleilu.hulu.view;

import com.xiaoleilu.hulu.Response;

/**
 * 普通文本内容
 * @author Looly
 *
 */
public class TextView extends ContentTypeView{
	
	//---------------------------------------------------------- Constructor start
	public TextView() {
		this.contentType = Response.CONTENT_TYPE_TEXT;
	}
	
	public TextView(String text) {
		super(text, Response.CONTENT_TYPE_TEXT);
	}
	//---------------------------------------------------------- Constructor end
}

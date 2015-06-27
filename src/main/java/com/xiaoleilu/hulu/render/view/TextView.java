package com.xiaoleilu.hulu.render.view;

import com.xiaoleilu.hulu.render.Render;

/**
 * 普通文本内容
 * @author Looly
 *
 */
public class TextView extends ContentTypeView{
	
	//---------------------------------------------------------- Constructor start
	public TextView() {
	}
	
	public TextView(String text) {
		super(text, Render.CONTENT_TYPE_TEXT);
	}
	//---------------------------------------------------------- Constructor end
}

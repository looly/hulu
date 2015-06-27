package com.xiaoleilu.hulu.render.view;

import com.xiaoleilu.hulu.render.Render;

/**
 * JSON内容
 * @author Looly
 *
 */
public class JsonView extends ContentTypeView{
	
	public JsonView() {
	}
	
	public JsonView(String text) {
		this.text = text;
	}
	
	@Override
	public void render() {
		Render.renderJson(text);
	}
}

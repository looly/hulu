package com.xiaoleilu.hulu.render.view;

import com.xiaoleilu.hulu.Request;
import com.xiaoleilu.hulu.Response;
import com.xiaoleilu.hutool.json.JSON;

/**
 * JSON内容
 * @author Looly
 *
 */
public class JsonView extends ContentTypeView{
	
	public JsonView(JSON json) {
		this(json.toString());
	}
	
	public JsonView(String text) {
		this.text = text;
		this.contentType = Request.isIE() ? Response.CONTENT_TYPE_JSON_IE : Response.CONTENT_TYPE_JSON;
	}
	
}

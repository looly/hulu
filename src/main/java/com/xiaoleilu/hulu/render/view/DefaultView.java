package com.xiaoleilu.hulu.render.view;

import com.xiaoleilu.hulu.render.Render;
import com.xiaoleilu.hutool.json.JSON;
import com.xiaoleilu.hutool.json.JSONSupport;

/**
 * 默认的视图<br>
 * 当Hulu不能识别返回值时，使用默认视图<br>
 * 默认视图会根据返回值类型，自动响应客户端相应的类型
 * @author Looly
 *
 */
public class DefaultView implements View{
	
	private Object obj;
	
	public DefaultView(Object obj) {
		this.obj = obj;
	}
	
	@Override
	public void render() {
		if(null == obj){
			return;
		}
		
		//自动设别JSON
		if(obj instanceof JSONSupport || obj instanceof JSON){
			Render.renderJson(obj.toString());
			return;
		}
		
		//如果非View对象，直接返回内容做为HTML
		Render.renderHtml(obj.toString());
	}
	
	/**
	 * 包装对象为View
	 * @param obj 对象
	 * @return DefaultView
	 */
	public static DefaultView wrap(Object obj){
		return new DefaultView(obj);
	}
	
	@Override
	public String toString() {
		return obj.toString();
	}
}

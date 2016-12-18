package com.xiaoleilu.hulu.view;

import java.io.File;

import com.xiaoleilu.hutool.json.JSON;
import com.xiaoleilu.hutool.json.JSONSupport;
import com.xiaoleilu.hutool.util.StrUtil;

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
		
		//识别已知返回值类型
		if(obj instanceof JSONSupport || obj instanceof JSON){
			new JsonView(obj.toString()).render();
		}else if(obj instanceof File){
			new FileView((File)obj).render();
		}else if(obj instanceof CharSequence){
			String objStr = ((CharSequence)obj).toString();
			if(null != objStr){
				objStr = objStr.trim();
				if(StrUtil.startWithIgnoreCase(objStr, "jsp:")){
					new JspView(objStr).render();
				}else if(StrUtil.startWithIgnoreCase(objStr, "velocity:")){
					new VelocityView(objStr).render();
				}else if(StrUtil.startWithIgnoreCase(objStr, "redirect:")){
					new RedirectView(objStr).render();
				}else if(StrUtil.startWithIgnoreCase(objStr, "forward:")){
					new ForwardView(objStr).render();
				}else{
					new TextView(objStr).render();
				}
			}
		}else{
			//如果非View对象，直接返回内容做为HTML
			new HtmlView(obj.toString()).render();
		}
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

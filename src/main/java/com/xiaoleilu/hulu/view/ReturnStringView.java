package com.xiaoleilu.hulu.view;

import cn.hutool.core.util.StrUtil;

/**
 * 返回值为String时的View处理，根据前缀判断需要响应对象的类型
 * @author Looly
 *
 */
public class ReturnStringView implements View{
	
	private CharSequence returnString;
	
	public ReturnStringView(CharSequence returnString) {
		this.returnString = returnString;
	}
	
	@Override
	public void render() {
		if(null == returnString){
			return;
		}
		
		String objStr = returnString.toString();
		if(null != objStr){
			objStr = objStr.trim();
			if(StrUtil.startWithIgnoreCase(objStr, "jsp:")){
				new JspView(objStr).render();
			}else if(StrUtil.startWithIgnoreCase(objStr, "velocity:")){
				new TemplateView(objStr).render();
			}else if(StrUtil.startWithIgnoreCase(objStr, "redirect:")){
				new RedirectView(objStr).render();
			}else if(StrUtil.startWithIgnoreCase(objStr, "forward:")){
				new ForwardView(objStr).render();
			}else{
				new TextView(objStr).render();
			}
		}
	}
	
	@Override
	public String toString() {
		return returnString.toString();
	}

}

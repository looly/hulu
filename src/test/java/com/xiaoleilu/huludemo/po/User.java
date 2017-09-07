package com.xiaoleilu.huludemo.po;

import com.xiaoleilu.hutool.json.JSONUtil;

/**
 * 用户PO<br>
 * 我习惯性的吧所有POJO对象分为两类，PO和VO，PO表示Persistent Object， VO表示Value Object。<br>
 * 前者用于持久化，后者用户做为各层数据传递的载体
 * @author loolly
 *
 */
public class User {
	
	/** 用户名 */
	private String name;
	/** 密码，原谅我使用明文，这只是个demo，不要那么认真么…… */
	private String pass;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	
	/**
	 * 我喜欢把所有PO的toString方法用JSON的方式数据，一方面是好看，二是这个方法可能会在某些地方直接用。<br>
	 * 我喜欢FastJson的原因其实只有一个：没有第三方依赖包，同样原因我讨厌Velocity，喜欢HTTL这个模板框架。
	 */
	@Override
	public String toString() {
		return JSONUtil.toJsonStr(this);
	}
}

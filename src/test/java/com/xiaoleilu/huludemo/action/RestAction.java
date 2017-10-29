package com.xiaoleilu.huludemo.action;

import com.xiaoleilu.hulu.Request;
import com.xiaoleilu.hutool.lang.Console;

/**
 * RestFull接口测试
 * @author Looly
 *
 */
public class RestAction {
	public String restTest(){
		String body = Request.getBody();
		
		Console.log(body);
		return "OK";
	}
}

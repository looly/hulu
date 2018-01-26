package com.xiaoleilu.huludemo.client;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class RestClientTest {
	public static void main(String[] args) {
		JSONObject paramJson = JSONUtil.createObj().put("name", "张三").put("age", 24);
		HttpUtil.post("http://localhost:8090/rest/restTest", paramJson.toString());
	}
}

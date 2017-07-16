package com.xiaoleilu.huludemo.client;

import com.xiaoleilu.hutool.http.HttpUtil;
import com.xiaoleilu.hutool.json.JSONObject;
import com.xiaoleilu.hutool.json.JSONUtil;

public class RestClientTest {
	public static void main(String[] args) {
		JSONObject paramJson = JSONUtil.createObj().put("name", "张三").put("age", 24);
		HttpUtil.post("http://localhost:8090/rest/restTest", paramJson.toString());
	}
}

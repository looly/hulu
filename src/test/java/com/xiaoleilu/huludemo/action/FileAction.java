package com.xiaoleilu.huludemo.action;

import java.io.IOException;

import com.xiaoleilu.hulu.Request;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Console;
import cn.hutool.extra.servlet.multipart.UploadFile;

public class FileAction {
	public String upload() throws IOException {
		UploadFile[] files = Request.getMultipart().getFiles("file");
		for (UploadFile uploadFile : files) {
			Console.log(uploadFile.getFileName());
//			FileUtil.writeBytes(uploadFile.getFileContent(), "e:/uploadFiles/" + uploadFile.getFileName());
			Console.log(Base64.encode(uploadFile.getFileContent()));
		}
		return "OK";
	}
}

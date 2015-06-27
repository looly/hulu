package com.xiaoleilu.hulu.render.view;

import java.io.File;

import com.xiaoleilu.hulu.render.Render;

/**
 * 返回文件
 * @author Looly
 *
 */
public class FileView implements View{
	
	private File file;
	private String responseFileName;
	private int bufferSize;
	
	public FileView(File file, String responseFileName, int bufferSize) {
		this.file = file;
		this.responseFileName = responseFileName;
		this.bufferSize = bufferSize;
	}
	
	@Override
	public void render() {
		Render.renderFile(file, responseFileName, bufferSize);
	}
}

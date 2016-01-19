package com.xiaoleilu.hulu.render.view;

import java.io.File;

import com.xiaoleilu.hulu.render.Render;
import com.xiaoleilu.hutool.util.IoUtil;

/**
 * 返回文件
 * @author Looly
 *
 */
public class FileView implements View{
	
	private File file;
	private String responseFileName;
	private int bufferSize;
	
	//---------------------------------------------------------- Constructor start
	/**
	 * 构造，自定义文件、文件名、缓存
	 */
	public FileView() {
	}
	
	/**
	 * 构造
	 * @param file 文件
	 * @param responseFileName 响应给客户端的文件名
	 * @param bufferSize 缓存大小
	 */
	public FileView(File file, String responseFileName, int bufferSize) {
		this.file = file;
		this.responseFileName = responseFileName;
		this.bufferSize = bufferSize;
	}
	
	/**
	 * 构造，默认文件名
	 * @param file 文件
	 * @param bufferSize 缓存大小
	 */
	public FileView(File file, int bufferSize) {
		this(file, null, bufferSize);
	}
	
	/**
	 * 构造，默认缓存大小
	 * @param file 文件
	 * @param responseFileName 响应给客户端的文件名
	 */
	public FileView(File file, String responseFileName) {
		this(file, responseFileName, IoUtil.DEFAULT_BUFFER_SIZE);
	}
	
	/**
	 * 构造，默认文件名和缓存大小
	 * @param file 文件
	 */
	public FileView(File file) {
		this(file, null, IoUtil.DEFAULT_BUFFER_SIZE);
	}
	//---------------------------------------------------------- Constructor end
	
	//---------------------------------------------------------- Getters and Setters start
	/**
	 * @return 文件
	 */
	public File getFile() {
		return file;
	}
	/**
	 * 设置文件
	 * @param file 文件
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return 返回给客户端的文件名
	 */
	public String getResponseFileName() {
		return responseFileName;
	}
	/**
	 * 设置返回给客户端的文件名
	 * @param responseFileName 返回给客户端的文件名
	 */
	public void setResponseFileName(String responseFileName) {
		this.responseFileName = responseFileName;
	}

	/**
	 * @return 缓存大小
	 */
	public int getBufferSize() {
		return bufferSize;
	}
	/**
	 * 设置缓存
	 * @param bufferSize 缓存大小
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
	//---------------------------------------------------------- Getters and Setters end
	
	@Override
	public void render() {
		Render.renderFile(file, responseFileName, bufferSize);
	}
}

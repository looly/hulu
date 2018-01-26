package com.xiaoleilu.hulu.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.servlet.http.HttpServletResponse;

import com.xiaoleilu.hulu.HuluSetting;
import com.xiaoleilu.hulu.Response;
import com.xiaoleilu.hulu.exception.RenderException;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;

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
	 * 构造，默认文件名和缓存大小
	 * @param file 文件
	 */
	public FileView(File file) {
		this(file, null, IoUtil.DEFAULT_BUFFER_SIZE);
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
	 * 构造，默认文件名
	 * @param file 文件
	 * @param bufferSize 缓存大小
	 */
	public FileView(File file, int bufferSize) {
		this(file, null, bufferSize);
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
		if(null == this.file){
			throw new RenderException("File param is null !");
		}
		if(false == file.exists()){
			throw new RenderException(StrUtil.format("File [{}] not exist !", file));
		}
		if(false == file.isFile()){
			throw new RenderException(StrUtil.format("File [{}] not a file !", file));
		}
		long fileLength = file.length();
		if(fileLength > Integer.MAX_VALUE) {
			throw new RenderException("File [" + file.getName() + "] is too large, file size: [" + fileLength + "]");
		}
		
		if (StrUtil.isBlank(responseFileName)) {
			// 如果未指定文件名，使用原文件名
			responseFileName = file.getName();
		}
		responseFileName = HttpUtil.encode(responseFileName, HuluSetting.charset);
		
		final HttpServletResponse response = Response.getServletResponse();
		response.addHeader("Content-disposition", "attachment; filename=" + responseFileName);
		response.setContentType("application/octet-stream");
		response.setContentLength((int)fileLength);
		
		try {
			Response.write(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new RenderException(StrUtil.format("File [{}] not exist !", file), e);
		}
	}
}

package com.xiaoleilu.hulu.view;

import java.io.InputStream;

import com.xiaoleilu.hulu.Response;
import com.xiaoleilu.hulu.exception.RenderException;

/**
 * 返回二进制流
 * @author Looly
 *
 */
public class StreamView implements View{
	
	private InputStream in;
	private String contentType;
	private int bufferSize;
	
	//---------------------------------------------------------- Constructor start
	/**
	 * 构造
	 */
	public StreamView() {
	}
	
	/**
	 * 构造，默认文件名和缓存大小
	 * @param file 文件
	 */
	public StreamView(InputStream in) {
		this(in, "application/octet-stream");
	}
	
	/**
	 * 构造
	 * @param in 输入流
	 * @param contentType 类型
	 */
	public StreamView(InputStream in, String contentType) {
		this.in = in;
		this.contentType = contentType;
	}
	//---------------------------------------------------------- Constructor end
	
	//---------------------------------------------------------- Getters and Setters start
	/**
	 * @return 流
	 */
	public InputStream getStream() {
		return in;
	}
	/**
	 * 设置流
	 * @param in 流
	 */
	public void setStream(InputStream in) {
		this.in = in;
	}
	
	/**
	 * @return Content-Type
	 */
	public String getContentType() {
		return contentType;
	}
	/**
	 * 设置Content-Type
	 * @param contentType Content-Type
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
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
		if(null == this.in){
			throw new RenderException("InputStream is null !");
		}
		
		Response.setContentType(this.contentType);
		Response.write(this.in, this.bufferSize);
	}
}

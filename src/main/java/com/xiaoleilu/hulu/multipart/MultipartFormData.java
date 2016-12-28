package com.xiaoleilu.hulu.multipart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.xiaoleilu.hutool.util.ArrayUtil;

/**
 * HttpRequest解析器
 * 
 * @author jodd.org
 */
public class MultipartFormData {

	/** 请求参数 */
	private Map<String, String[]> requestParameters = new HashMap<String, String[]>();
	/** 请求文件 */
	private Map<String, UploadFile[]> requestFiles = new HashMap<String, UploadFile[]>();

	/** 是否解析完毕 */
	private boolean loaded;
	
	private UploadSetting setting;

	// --------------------------------------------------------------------- Constructor start
	public MultipartFormData() {
		this(null);
	}
	
	/**
	 * 构造
	 * @param uploadSetting 上传设定
	 */
	public MultipartFormData(UploadSetting uploadSetting) {
		this.setting = uploadSetting == null ? new UploadSetting() : uploadSetting;
	}
	// --------------------------------------------------------------------- Constructor end
	
	/**
	 * 解析上传文件和表单数据
	 * @param request Http请求
	 * @throws IOException
	 */
	public void parseRequest(HttpServletRequest request) throws IOException {
		parseRequestStream(request.getInputStream(), request.getCharacterEncoding());
	}

	/**
	 * 提取上传的文件和表单数据
	 * @param inputStream HttpRequest流
	 * @param encoding 编码
	 * @throws IOException
	 */
	public void parseRequestStream(InputStream inputStream, String encoding) throws IOException {
		setLoaded();

		MultipartRequestInputStream input = new MultipartRequestInputStream(inputStream);
		input.readBoundary();
		while (true) {
			UploadFileHeader header = input.readDataHeader(encoding);
			if (header == null) {
				break;
			}
			
			if (header.isFile == true) {
				//文件类型的表单项
				String fileName = header.fileName;
				if (fileName.length() > 0 && header.contentType.contains("application/x-macbinary")) {
					input.skipBytes(128);
				}
				UploadFile newFile = new UploadFile(header, setting);
				newFile.processStream(input);
				
				putFile(header.formFieldName, newFile);
			} else {
				//标准表单项
				ByteArrayOutputStream fbos = new ByteArrayOutputStream(1024);
				input.copy(fbos);
				String value = (encoding != null) ? new String(fbos.toByteArray(), encoding) : new String(fbos.toByteArray());
				putParameter(header.formFieldName, value);
			}

			input.skipBytes(1);
			input.mark(1);

			// read byte, but may be end of stream
			int nextByte = input.read();
			if (nextByte == -1 || nextByte == '-') {
				input.reset();
				break;
			}
			input.reset();
		}
	}

	// ---------------------------------------------------------------- parameters
	/**
	 * 返回单一参数值，如果有多个只返回第一个
	 * 
	 * @param paramName 参数名
	 * @return null未找到，否则返回值
	 */
	public String getParam(String paramName) {
		if (requestParameters == null) {
			return null;
		}
		String[] values = requestParameters.get(paramName);
		if (ArrayUtil.isNotEmpty(values)) {
			return values[0];
		}
		return null;
	}

	/**
	 * @return 获得参数名集合
	 */
	public Set<String> getParamNames() {
		if (requestParameters == null) {
			return Collections.emptySet();
		}
		return requestParameters.keySet();
	}

	/**
	 * 获得数组表单值
	 * @param paramName 参数名
	 * @return 数组表单值
	 */
	public String[] getArrayParam(String paramName) {
		if (requestParameters == null) {
			return null;
		}
		return requestParameters.get(paramName);
	}
	
	/**
	 * @return 所有属性的集合
	 */
	public Map<String, String[]> getParamMap() {
		return requestParameters;
	}

	//--------------------------------------------------------------------------- Files parameters
	/**
	 * @param paramName 文件参数名称
	 * @return 上传的文件， 如果无为null
	 */
	public UploadFile getFile(String paramName) {
		UploadFile[] values = getFiles(paramName);
		if ((values != null) && (values.length > 0)) {
			return values[0];
		}
		return null;
	}

	/**
	 * 获得某个属性名的所有文件<br>
	 * 当表单中两个文件使用同一个name的时候
	 * @param paramName 属性名
	 * @return 上传的文件列表
	 */
	public UploadFile[] getFiles(String paramName) {
		if (requestFiles == null) {
			return null;
		}
		return requestFiles.get(paramName);
	}

	/**
	 * @return 上传的文件属性名集合
	 */
	public Set<String> getFileParamNames() {
		if (requestFiles == null) {
			return Collections.emptySet();
		}
		return requestFiles.keySet();
	}
	
	/**
	 * @return 文件映射
	 */
	public Map<String, UploadFile[]> getFileMap() {
		return this.requestFiles;
	}
	
	//--------------------------------------------------------------------------- Load
	/**
	 * @return 如果流已被解析返回true
	 */
	public boolean isLoaded() {
		return loaded;
	}

	// ---------------------------------------------------------------- Private method start
	private void putFile(String name, UploadFile value) {
		UploadFile[] fileUploads = requestFiles.get(name);
		fileUploads = fileUploads == null ? new UploadFile[] { value } : ArrayUtil.append(fileUploads, value);
		requestFiles.put(name, fileUploads);
	}

	private void putParameter(String name, String value) {
		String[] params = requestParameters.get(name);
		params  = params == null ? new String[] { value } : ArrayUtil.append(params, value);
		requestParameters.put(name, params);
	}

	/**
	 * 设置使输入流为解析状态，如果已解析，则抛出异常
	 * @throws IOException
	 */
	private void setLoaded() throws IOException {
		if (loaded == true) {
			throw new IOException("Multi-part request already parsed.");
		}
		loaded = true;
	}
	// ---------------------------------------------------------------- Private method end
}

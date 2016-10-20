package com.xiaoleilu.hulu.view;

import java.io.File;
import java.io.IOException;

import com.xiaoleilu.hulu.HuluSetting;
import com.xiaoleilu.hulu.exception.ActionRuntimeException;
import com.xiaoleilu.hutool.util.FileUtil;

/**
 * HTML文件，读取文件内容作为HTML返回
 * @author Looly
 *
 */
public class HtmlFileView extends HtmlView{
	public HtmlFileView(File file) {
		super();
		try {
			this.text = FileUtil.readString(file, HuluSetting.charset);
		} catch (IOException e) {
			throw new ActionRuntimeException(e);
		}
	}
}

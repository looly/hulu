package com.xiaoleilu.huludemo.action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;

import com.xiaoleilu.hulu.Request;
import com.xiaoleilu.hulu.Response;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

/**
 * Excel生成后下载接口测试
 * 
 * @author Looly
 *
 */
public class ExcelAction {

	/**
	 * 生成并下载
	 * 
	 * @throws IOException
	 */
	public void download() throws IOException {
		String body = Request.getBody();

		Console.log("Conteny-Type: {}", Request.getHeader("Content-Type"));
		Console.log(body);

		List<?> row1 = CollUtil.newArrayList("aaaaa", "bb", "cc", "dd", DateUtil.date(), 3.22676575765);
		List<?> row2 = CollUtil.newArrayList("aa1", "bb1", "cc1", "dd1", DateUtil.date(), 250.7676);
		List<?> row3 = CollUtil.newArrayList("aa2", "bb2", "cc2", "dd2", DateUtil.date(), 0.111);
		List<?> row4 = CollUtil.newArrayList("aa3", "bb3", "cc3", "dd3", DateUtil.date(), 35);
		List<?> row5 = CollUtil.newArrayList("aa4", "bb4", "cc4", "dd4", DateUtil.date(), 28.00);

		List<List<?>> rows = CollUtil.newArrayList(row1, row2, row3, row4, row5);
		ExcelWriter writer = ExcelUtil.getWriter(true);
		writer.passCurrentRow();
		writer.merge(rows.size(), "sdddf测试接口");
		writer.write(rows);

		Response.setContentType("application/vnd.ms-excel;charset=utf-8");
		Response.setHeader("Content-disposition", "attachment;filename=\"test.xlsx\"");
		ServletOutputStream out = Response.getOutputStream();
		writer.flush(out, true);
		writer.close();
	}
}

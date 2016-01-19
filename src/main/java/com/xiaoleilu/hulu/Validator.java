package com.xiaoleilu.hulu;

import com.xiaoleilu.hulu.exception.ValidateException;
import com.xiaoleilu.hutool.util.ReUtil;
import com.xiaoleilu.hutool.util.StrUtil;

/**
 * 字段验证器
 * 
 * @author Looly
 *
 */
public class Validator {
	/**
	 * 验证是否为空<br>
	 * 对于String类型判定是否为empty(null 或 "")<br>
	 * 
	 * @param formValue 表单值
	 * @param errorMsg 验证错误的信息
	 * @throws ValidateException
	 */
	public static <T> void validateEmpty(T formValue, String errorMsg) throws ValidateException {
		if (null == formValue) {
			throw new ValidateException(errorMsg);
		}
		if (formValue instanceof String && StrUtil.isEmpty((String) formValue)) {
			throw new ValidateException(errorMsg);
		}
	}

	/**
	 * 验证是否与指定无效值相等<br>
	 * 当表单数据和无效值都为null时抛出验证异常<br>
	 * 表单数据与无效值相等时抛出验证异常
	 * 
	 * @param formValue 表单值
	 * @param badValue 无效值
	 * @param errorMsg 错误信息
	 * @throws ValidateException
	 */
	public static <T> void validateEqual(T formValue, T badValue, String errorMsg) throws ValidateException {
		if (null == formValue) {
			if (null == badValue) {
				throw new ValidateException(errorMsg);
			}
		} else if (formValue.equals(badValue)) {
			throw new ValidateException(errorMsg);
		}
	}

	/**
	 * 验证是否非空且与指定无效值相等<br>
	 * 当表单数据为空时抛出验证异常<br>
	 * 当表单数据和无效值都为null时抛出验证异常<br>
	 * 表单数据与无效值相等时抛出验证异常
	 * 
	 * @param formValue 表单值
	 * @param badValue 无效值
	 * @param errorMsg 错误信息
	 * @throws ValidateException
	 */
	public static <T> void validateEmptyEqual(T formValue, T badValue, String errorMsg) throws ValidateException {
		validateEmpty(formValue, errorMsg);
		validateEqual(formValue, badValue, errorMsg);
	}

	/**
	 * 通过正则表达式验证
	 * 
	 * @param regex 正则
	 * @param formValue 表单值
	 * @param errorMsg 验证错误的信息
	 * @throws ValidateException
	 */
	public static void validateByRegex(String regex, String formValue, String errorMsg) throws ValidateException {
		validateEmpty(formValue, errorMsg);
		if (false == ReUtil.isMatch(regex, formValue)) {
			throw new ValidateException(errorMsg);
		}
	}

	/**
	 * 验证是否为英文字母 、数字和下划线
	 * 
	 * @param formValue 表单值
	 * @param errorMsg 验证错误的信息
	 * @throws ValidateException
	 */
	public static void validateGeneral(String formValue, String errorMsg) throws ValidateException {
		validateByRegex("^\\w+$", formValue, errorMsg);
	}

	/**
	 * 验证是否为给定长度范围的英文字母 、数字和下划线
	 * 
	 * @param formValue 表单值
	 * @param min 最小长度，负数自动识别为0
	 * @param max 最大长度，0或负数表示不限制最大长度
	 * @param errorMsg 验证错误的信息
	 * @throws ValidateException
	 */
	public static void validateGeneral(String formValue, int min, int max, String errorMsg) throws ValidateException {
		String reg = "^\\w{" + min + "," + max + "}$";
		if (min < 0) {
			min = 0;
		}
		if (max <= 0) {
			reg = "^\\w{" + min + ",}$";
		}
		validateByRegex(reg, formValue, errorMsg);
	}

	/**
	 * 验证是否为给定最小长度的英文字母 、数字和下划线
	 * 
	 * @param formValue 表单值
	 * @param min 最小长度，负数自动识别为0
	 * @param errorMsg 验证错误的信息
	 * @throws ValidateException
	 */
	public static void validateGeneral(String formValue, int min, String errorMsg) throws ValidateException {
		validateGeneral(formValue, min, 0, errorMsg);
	}

	/**
	 * 验证是否为数字
	 * 
	 * @param formValue 表单值
	 * @param errorMsg 验证错误的信息
	 * @throws ValidateException
	 */
	public static void validateNumbers(String formValue, String errorMsg) throws ValidateException {
		validateByRegex("\\d+", formValue, errorMsg);
	}
	
	/**
	 * 验证是否为货币
	 * 
	 * @param formValue 表单值
	 * @param errorMsg 验证错误的信息
	 * @throws ValidateException
	 */
	public static void validateMoney(String formValue, String errorMsg) throws ValidateException {
		validateByRegex("^(\\d+(?:\\.\\d+)?)$", formValue, errorMsg);
	}

	/**
	 * 验证是否为邮政编码（中国）
	 * 
	 * @param formValue 表单值
	 * @param errorMsg 验证错误的信息
	 * @throws ValidateException
	 */
	public static void validateZipCode(String formValue, String errorMsg) throws ValidateException {
		validateByRegex("\\d{6}", formValue, errorMsg);
	}

	/**
	 * 验证是否为可用邮箱地址
	 * 
	 * @param formValue 表单值
	 * @param errorMsg 验证错误的信息
	 * @throws ValidateException
	 */
	public static void validateEmail(String formValue, String errorMsg) throws ValidateException {
		validateByRegex("(\\w|.)+@\\w+(\\.\\w+){1,2}", formValue, errorMsg);
	}

	/**
	 * 验证是否为手机号码（中国）
	 * 
	 * @param formValue 表单值
	 * @param errorMsg 验证错误的信息
	 * @throws ValidateException
	 */
	public static void validateMobile(String formValue, String errorMsg) throws ValidateException {
		validateByRegex("1\\d{10}", formValue, errorMsg);
	}

	/**
	 * 验证是否为身份证号码（18位中国）<br>
	 * 出生日期只支持到到2999年
	 * 
	 * @param formValue 表单值
	 * @param errorMsg 验证错误的信息
	 * @throws ValidateException
	 */
	public static void validateCitizenIdNumber(String formValue, String errorMsg) throws ValidateException {
		validateByRegex("[1-9]\\d{5}[1-2]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}(\\d|X|x)", formValue, errorMsg);
	}

	/**
	 * 验证验证是否为生日<br>
	 * 
	 * @param formValue 表单值
	 * @param errorMsg 验证错误的信息
	 * @throws ValidateException
	 */
	public static void validateBirthday(String formValue, String errorMsg) throws ValidateException {
		validateByRegex("(\\d{4})(/|-|\\.)(\\d{1,2})(/|-|\\.)(\\d{1,2})日?$", formValue, errorMsg);

		int year = Integer.parseInt(formValue.substring(0, 4));
		int month = Integer.parseInt(formValue.substring(5, 7));
		int day = Integer.parseInt(formValue.substring(8, 10));
		
		if (month < 1 || month > 12) {
			throw new ValidateException(errorMsg);
		}
		if (day < 1 || day > 31) {
			throw new ValidateException(errorMsg);
		}
		if ((month == 4 || month == 6 || month == 9 || month == 11) && day == 31) {
			throw new ValidateException(errorMsg);
		}
		if (month == 2) {
			boolean isleap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
			if (day > 29 || (day == 29 && !isleap)) throw new ValidateException(errorMsg);
		}
	}

	/**
	 * 验证是否为IPV4地址
	 * 
	 * @param formValue 表单值
	 * @param errorMsg 验证错误的信息
	 * @throws ValidateException
	 */
	public static void validateIpv4(String formValue, String errorMsg) throws ValidateException {
		validateByRegex("([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])", formValue, errorMsg);
	}

	/**
	 * 验证是否为URL
	 * 
	 * @param formValue 表单值
	 * @param errorMsg 验证错误的信息
	 * @throws ValidateException
	 */
	public static void validateUrl(String formValue, String errorMsg) throws ValidateException {
		validateByRegex("(https://|http://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?", formValue, errorMsg);
	}
	
	/**
	 * 验证是否为汉字
	 * 
	 * @param formValue 表单值
	 * @param errorMsg 验证错误的信息
	 * @throws ValidateException
	 */
	public static void validateChinese(String formValue, String errorMsg) throws ValidateException {
		validateByRegex("^" + ReUtil.RE_CHINESE + "+$", formValue, errorMsg);
	}
	
	/**
	 * 验证是否为中文字、英文字母、数字和下划线
	 * 
	 * @param formValue 表单值
	 * @param errorMsg 验证错误的信息
	 * @throws ValidateException
	 */
	public static void validateStr(String formValue, String errorMsg) throws ValidateException {
		validateByRegex("^[\\u0391-\\uFFE5\\w]+$", formValue, errorMsg);
	}
	
	/**
	 * 验证是否为UUID
	 * 
	 * @param formValue 表单值
	 * @param errorMsg 验证错误的信息
	 * @throws ValidateException
	 */
	public static void validateUUID(String formValue, String errorMsg) throws ValidateException {
		validateByRegex("^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$", formValue, errorMsg);
	}
	
	public static void main(String[] args) throws ValidateException {
	}
}

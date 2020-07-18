package com.yonyou.dms.manage.tool;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * 公共工具类
 * 
 * @author guohao
 *
 */
@SuppressWarnings("rawtypes")
public class CommonUtils {
	
	private static final Logger logger = Logger.getLogger(CommonUtils.class);

	/**
	 * 判断集合是否为空
	 * 
	 * @param coll
	 * @return
	 */
	public static boolean isNull(Collection coll) {
		if (coll == null || coll.size() <= 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str) {
		if (str == null || "".equals(str.trim()) || "null".equalsIgnoreCase(str)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取String
	 * 
	 * @param obj
	 * @return
	 */
	public static String getString(Object obj) {
		if (obj == null || "".equals(obj.toString().trim()) || "null".equalsIgnoreCase(obj.toString().trim())) {
			return "";

		}
		return String.valueOf(obj).trim();
	}
	
	/**
	 * 获取字符串字节数
	 * @param str
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static Integer getByteLength(String str) {
		try {
			return str.getBytes("UTF-8").length;
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException", e);
		}
		return null;
	}
	
	/**
	 * 判断字符串是否是数字（正数或小数）
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
        return str.matches("^[0-9]+(.[0-9]+)?$");
	}
	
	/**
	 * 设置文件头
	 * @param response
	 * @param fileName
	 */
	public static void setResponseHeader(HttpServletResponse response, String fileName) {
		try {
			try {
				fileName = new String(fileName.getBytes(), "ISO8859-1");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				logger.error("excel导出错误：", e);
			}
			response.setContentType("application/octet-stream;charset=ISO8859-1");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			response.addHeader("Pargam", "no-cache");
			response.addHeader("Cache-Control", "no-cache");
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("excel导出错误：", ex);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(getByteLength("你好"));
		System.out.println(getByteLength("AB12"));
		System.out.println(isNumber("AB12"));
		System.out.println(isNumber("12"));
		System.out.println(isNumber("12.01"));
		System.out.println(isNumber("12。01发"));
	}

}

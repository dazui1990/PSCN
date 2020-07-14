/*
* Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
*
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : cmol.common.function
*
* @File name : DateUtil.java
*
* @Author : ChenPeiYu
*
* @Date : 2016年3月28日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2016年3月28日    ChenPeiYu    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/

package com.yonyou.dms.function.utils.common;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.yonyou.dms.function.common.CommonConstants;
import com.yonyou.dms.function.exception.UtilException;

/**
 * 
* 日期格式化函数
* @author zhangxc
* @date 2016年6月29日
 */

public class DateUtil {

    
    /**
     * 对日期时间字符串转化为日期对象，格式：yyyy-MM-dd HH:mm:ss
    * @author zhangxc
    * @date 2016年6月29日
    * @param dateTime
    * @return
     */
    public static Date parseDefaultDateTime(String dateTime){
        return parseDate(dateTime,CommonConstants.FULL_DATE_TIME_FORMAT);
    }
    /**
     * 对日期时间字符串转化为日期对象，格式：yyyy-MM-dd
    * TODO description
    * @author zhangxc
    * @date 2016年6月29日
    * @param dateTime
    * @return
     */
    public static Date parseDefaultDate(String dateTime){
        return parseDate(dateTime,CommonConstants.SIMPLE_DATE_FORMAT);
    }
    
    /**
    * 对日期时间字符串转化为日期对象，格式：yyyy-MM
    * @author zhanshiwei
    * @date 2016年10月11日
    * @param dateTime
    * @return
    */
    	
    public static Date parseDefaultDateMonth(String dateTime){
        return parseDate(dateTime,CommonConstants.SIMPLE_DATE_MONTH_FORMAT);
    }
    
    /**
     * 
    * 格式化默认日期,格式：yyyy-MM-dd
    * @author zhangxc
    * @date 2016年6月29日
    * @param date
    * @return
     */
    public static String formatDefaultDate(Date date) {
        return formatDateByFormat(date,CommonConstants.SIMPLE_DATE_FORMAT);
    }
    
    /**
     * 
    * 格式化默认日期,格式：yyyy-MM-dd HH:mm
    * @author zhangxc
    * @date 2016年6月29日
    * @param date
    * @return
     */
    public static String formatDefaultDateTime(Date date) {
        return formatDateByFormat(date,CommonConstants.SIMPLE_DATE_TIME_FORMAT);
    }
    
    /**
     * 
    * 将当前时间截掉时分秒
    * @author zhangxc
    * @date 2016年12月21日
    * @param date
    * @return
     */
    public static Date truncDate(Date date){
        String formatDate = formatDefaultDateTime(date);
        return parseDefaultDate(formatDate);
    }
    /**
    * 字符串转化时间
    * @author zhangxc
    * @date 2016年6月29日
    * @param time
    * @param format
    * @return
    * @throws Exception
    */
    	
    public static Date parseDate(String time, String format){
        if(StringUtils.isNullOrEmpty(time)){
            return null;
        }
        if (StringUtils.isNullOrEmpty(format)) {
            throw new UtilException("日期格式不正确");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.parse(time);
        } catch (Exception e) {
            throw new UtilException("日期转换出错：" + e.getMessage(), e);
        }
    }

    /*
     * @author LiaoYuzhi 根据当前日期计算n天后的日期
     * @date 2016年3月28日
     * @param date
     * @param n
     * @return
     */

    public static Date addDay(Date date, int n) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, n);
        Date destDay = c.getTime();
        return destDay;
    }

    /*
     * @author LiaoYuzhi 以指定的格式来格式化日期
     * @date 2016年3月28日
     * @param date
     * @param format
     * @return
     */

    public static String formatDateByFormat(Date date, String format) {
        String result = StringUtils.EMPTY_STRING;
        if (date != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                result = sdf.format(date);
            } catch (Exception ex) {
                throw new UtilException("日期转换出错：" + ex.getMessage(), ex);
            }
        }
        return result;
    }
    
    /**
    * 加1天
    * @author jcsi
    * @date 2016年8月29日
    * @param param
    * @return
     */
    public static Date addOneDay(Object param){
        return addDay(parseDefaultDate(param+""),1);        
        //return formatDefaultDate(value);
    }
    
    
    /**
    *  指定日期的第一天
    * @author zhanshiwei
    * @date 2016年9月30日
    * @param date
    * @return
    */
    	
    public static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR),
                     calendar.get(Calendar.MONTH), 1);
        return parseDefaultDate(formatDefaultDate(calendar.getTime()));
    }
    
    /**
    * 指定日期的最后一天
    * @author zhanshiwei
    * @date 2016年9月30日
    * @param date
    * @return
    */
    	
    public static Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR),
                     calendar.get(Calendar.MONTH), 1);
        calendar.roll(Calendar.DATE, -1);
        return parseDefaultDate(formatDefaultDate(calendar.getTime()));
    }
    
    
    /**
    * 获取下一个月的第一天
    * @author zhanshiwei
    * @date 2016年9月30日
    * @param date
    * @return
    */
    	
    public static Date getPerFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return parseDefaultDate(formatDefaultDate(calendar.getTime()));
    }
    
    /**
    * 比较两个时间相差多少分钟
    * @author jcsi
    * @date 2016年10月28日
    * @param endDate  结束时间
    * @param nowDate  开始时间
    * @return
     */
    public static Long toCompareTime(Date endDate,Date nowDate){
        long nd = 1000 * 24 * 60 * 60l;
        long nh = 1000 * 60 * 60l;
        long nm = 1000 * 60l;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return (Long)day*24*60+hour*60+min;
    }
    
    /**
     * 截取想要的日期类型-String
     * @author ZhengHe
     * @date 2016年12月16日
     * @param sourceDateTime
     * @param souceDateFormat
     * @param destionDataFormat
     * @return
     */
     	
     public static String formatDateStrByFormat(String sourceDateTime,String souceDateFormat,String destionDataFormat){
     	Date sourceDate = parseDate(sourceDateTime,souceDateFormat);
     	return formatDateByFormat(sourceDate,destionDataFormat);
     }
     
     /**
      * 
     * 获取下一个月日期
     * @author zhangxc
     * @date 2017年1月3日
     * @param year
     * @param month
     * @return
      */
     public static Calendar getNextMonthDate(int year,int month){
         Calendar cal = Calendar.getInstance();
         cal.set(year, month, 1);
         cal.add(Calendar.MONTH, 1);
         return cal;
     }
     
     /**
      *  获取当前时间对应年份
      */
     public static int getNowYear(){
    	 return Calendar.getInstance().get(Calendar.YEAR);
     }
     
     /**
      *  获取当前时间对应月份
      */
     public static int getNowMonth(){
    	 return Calendar.getInstance().get(Calendar.MONTH) + 1;
     }
     
     /**
      *  获取N+2 对应年份,月份
      */
     public static Map<String, Integer> getN2YearAndMonth(){
    	 Map<String, Integer> map = new HashMap<String, Integer>();
    	 Calendar cal = Calendar.getInstance();
    	 cal.add(Calendar.MONTH, 2);
    	 map.put("year", cal.get(Calendar.YEAR));
    	 map.put("month", cal.get(Calendar.MONTH) + 1);
    	 return map;
     }
     
     /**
      *  获取N+3 对应年份,月份
      */
     public static Map<String, Integer> getN3YearAndMonth(){
    	 Map<String, Integer> map = new HashMap<String, Integer>();
    	 Calendar cal = Calendar.getInstance();
    	 cal.add(Calendar.MONTH, 3);
    	 map.put("year", cal.get(Calendar.YEAR));
    	 map.put("month", cal.get(Calendar.MONTH) + 1);
    	 return map;
     }
     
     /**
      * 当前月
     * @author DuPengXin
     * @date 2017年1月19日
     * @param date
     * @param n
     * @return
     */
     	
     public static Date addMonth(Date date, int n) {
         Calendar c = Calendar.getInstance();
         c.setTime(date);
         c.add(Calendar.MONTH, n);
         Date destDay = c.getTime();
         return destDay;
     }
     
     
     /**
      * 根据开始月  结束月  获取中间所有月份 格式 'XXXX-XX','XXXX-XX'
      * @param beginMonthStr
      * @param endMonthStr
      * @return
      * @throws Exception
      */
 	public static String getMonthsByQuery(String beginMonthStr, String endMonthStr) throws Exception {
		String months = "";

		try {
			if (!StringUtils.isNullOrEmpty(beginMonthStr) 
					&& !StringUtils.isNullOrEmpty(endMonthStr)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");// 格式化为年月
				Calendar min = Calendar.getInstance();
				Calendar max = Calendar.getInstance();
				min.setTime(sdf.parse(beginMonthStr));
				max.setTime(sdf.parse(endMonthStr));

				Calendar curr = min;
				while (curr.before(max)) {
					months = months + "'" + sdf.format(curr.getTime()) + "',";
					curr.add(Calendar.MONTH, 1);
				}
				if (curr.get(Calendar.MONTH) == max.get(Calendar.MONTH)) {
					months = months + "'" + sdf.format(curr.getTime()) + "'";
				}
			}
		} catch (Exception e) {
			throw e;
		}

		return months;
	}
 	
    /**
     * 根据年度 季度  获取中间所有月份 格式 'XXXX-XX','XXXX-XX'
     * @param beginMonthStr
     * @param endMonthStr
     * @return
     * @throws Exception
     */
	public static String getMonthsByYearAndQuarter(String yearStr, String quarterStr) throws Exception {
		String months = "";

		//全年
		if(StringUtils.isNullOrEmpty(quarterStr)) {
			for (int i = 1; i <= 12; i++) {
				if(i == 1) {
					months = "'"+yearStr+"-"+String.format("%02d", i)+"'";
				}else {
					months += ",'"+yearStr+"-"+String.format("%02d", i)+"'";
				}
			}
		}
		//1季度
		else if("1".equals(quarterStr)){
			for (int i = 1; i <= 3; i++) {
				if(i == 1) {
					months = "'"+yearStr+"-"+String.format("%02d", i)+"'";
				}else {
					months += ",'"+yearStr+"-"+String.format("%02d", i)+"'";
				}
			}
		}
		//2季度
		else if("2".equals(quarterStr)){
			for (int i = 4; i <= 6; i++) {
				if(i == 4) {
					months = "'"+yearStr+"-"+String.format("%02d", i)+"'";
				}else {
					months += ",'"+yearStr+"-"+String.format("%02d", i)+"'";
				}
			}
		}else if("3".equals(quarterStr)){
			for (int i = 7; i <= 9; i++) {
				if(i == 7) {
					months = "'"+yearStr+"-"+String.format("%02d", i)+"'";
				}else {
					months += ",'"+yearStr+"-"+String.format("%02d", i)+"'";
				}
			}
		}
		else if("4".equals(quarterStr)){
			for (int i = 10; i <= 12; i++) {
				if(i == 10) {
					months = "'"+yearStr+"-"+String.format("%02d", i)+"'";
				}else {
					months += ",'"+yearStr+"-"+String.format("%02d", i)+"'";
				}
			}
		}

		return months;
	}
	
	
}

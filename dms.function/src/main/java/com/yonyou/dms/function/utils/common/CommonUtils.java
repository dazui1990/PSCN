/*
* Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
*
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : cmol.common.function
*
* @File name : HttpUtils.java
*
* @Author : zhangxianchao
*
* @Date : 2016年2月23日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2016年2月23日    zhangxianchao    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/

package com.yonyou.dms.function.utils.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.dms.function.common.CommonConstants;
import com.yonyou.dms.function.exception.UtilException;

import net.sourceforge.pinyin4j.PinyinHelper;

/*
*
* @author zhangxianchao
* CommonUtils
* @date 2016年2月23日
*/

public class CommonUtils {

    private static final String STR_NEW_LINE                   = "\n\r";
    private static final String STR_SEPARATED_BLANK            = StringUtils.BLANK_SPRING_STRING;
    // 定义日志接口
 	private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    /**
     * check if the list is null or empty
     */
    public static boolean isNullOrEmpty(Collection<?> list) {
        return list == null || list.isEmpty();
    }
    
    public static boolean isNullOrEmpty(Object[] objs) {
        return objs == null || objs.length <= 0;
    }

    /**
     * check if the map is null or empty
     */
    public static boolean isNullOrEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    

    /*
     * @author ChenPeiYu 根据汉字获取拼音首字母
     * @date 2016年3月9日
     * @param str
     * @return
     */
    public static String getHanyuPinyinString(String str) {
        StringBuffer convert = new StringBuffer(str.length() << 1);
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            // 获取拼音首字母
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert.append(pinyinArray[0].charAt(0));
            } else {
                convert.append(word);
            }
        }
        return convert.toString().toUpperCase();
    }

    /*
     * @author ChenPeiYu 获取5位订单号
     * @date 2016年3月10日
     * @param number
     * @return
     */
    public static String getFourOrderNo(int number) {
        String orderNo = String.valueOf(number);
        if (orderNo.length() <= CommonConstants.SYSTEM_ORDER_NO_NUMBER) {
            while (orderNo.length() < CommonConstants.SYSTEM_ORDER_NO_NUMBER) {
                orderNo = "0" + orderNo;
            }
            return orderNo;
        } else {
            return null;
        }
    }

    /*
     * @author LiuJun 产生紧凑型32位UUID-目前用于SAF taskId
     * @date 2016年3月28日
     * @return
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }
    
    /**
     * parse request and get data var html
     */
    public static String parseHttpServletRequest(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("date " + new Date() + "-----------------");
        sb.append(STR_NEW_LINE);
        sb.append("request " + request.getPathInfo() + "-----------------");
        sb.append(STR_NEW_LINE);
        sb.append("request method " + request.getMethod() + "-----------------");
        sb.append(STR_NEW_LINE);
        sb.append("request parameter" + "-----------------");
        sb.append(STR_NEW_LINE);
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String data = request.getParameter(name);
            sb.append("key " + name);
            sb.append(STR_SEPARATED_BLANK);
            sb.append("value " + data);
            sb.append(STR_NEW_LINE);
        }
        sb.append("header parameter" + "-----------------");
        sb.append(STR_NEW_LINE);
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String name = headers.nextElement();
            String data = request.getHeader(name);
            sb.append("key " + name);
            sb.append(STR_SEPARATED_BLANK);
            sb.append("value " + data);
            sb.append(STR_NEW_LINE);
        }
        sb.append("header parameter" + "-----------------");
        sb.append(STR_NEW_LINE);
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0) {
            for (Cookie cookie : cookies) {
                sb.append("key " + cookie.getName());
                sb.append(STR_SEPARATED_BLANK);
                sb.append("value " + cookie.getValue());
                sb.append(STR_NEW_LINE);
            }
        }
        return sb.toString();
    }

    

    /**
     * get local host
     */
    public static InetAddress getLocalHost() throws UtilException {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(),e);
            throw new UtilException("get local ip error", e);
        }
    }

    /**
     * get local host
     */
    public static InetAddress[] getAllLocalHosts() throws UtilException {
        String hostName = CommonUtils.getLocalHostName();
        if (StringUtils.isNullOrEmpty(hostName)) {
            throw new UtilException("cannot get local host");
        }
        try {
            return InetAddress.getAllByName(hostName);
        } catch (UnknownHostException e) {
            throw new UtilException(e);
        }

    }

    /**
     * get local host name
     */
    public static String getLocalHostName() throws UtilException {
        InetAddress address = getLocalHost();
        return address.getHostName();
    }
    /**
    * List<Map> 转换字符串 （[{DEALER_CODE=2100000, ROLE=10060001}, {DEALER_CODE=2100000, ROLE=10060002}, {DEALER_CODE=2100000, ROLE=10060003}]）
    * Map中有两列   DEALER_CODE和 ROLE   默认拼接第二个（ROLE）  的value值            
    * @author jcsi
    * @date 2016年7月12日
    * @param list   转换的list
    * @param separator  分隔符
    * @return
     */
    public static String listMapToString(List list, String separator) {   
        StringBuilder sb = new StringBuilder();
        String str="";
        if(list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map=(Map)list.get(i);   
                for (Map.Entry<String, Object> entry : map.entrySet()) {  
                    str=entry.getValue()+"";
                }              
                sb.append(str).append(separator);   
             } 
            return sb.toString().substring(0,sb.toString().length()-1);
        }else{
            return "";
        }
          
        
    }
    
    /**
    * 普通List转换字符串
    * @author jcsi
    * @date 2016年7月12日
    * @param list
    * @param separator
    * @return
     */
    public static String listToString(List list, String separator) {   
        StringBuilder sb = new StringBuilder();    
        for (int i = 0; i < list.size(); i++) {        
            sb.append(list.get(i)).append(separator);    }   
        return sb.toString().substring(0,sb.toString().length()-1);
    }
    
    
}
